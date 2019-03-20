package com.skies.utils;

import com.csvreader.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 文件操作类
 * 
 * @see 提供文件的增删改查以及对资源文件的访问<br/>
 *      关于所有方法中文件路径使用方式强调:<br/>
 *      如果是从项目中的classes目录中查找 则文件路径的第一位应该是 '!'
 *      比如查找classes下的example包中的1.properties 就是!examples/1.properties 也可输入绝对路径
 */
public class FileUtils {

	private static String defaultPath = "!config/environment/common.properties";

	/**
	 * 加载指定的properties文件
	 * 
	 * @see 加载指定的properties文件
	 * @param path
	 *            文件所在路径
	 * @return java.util.Properties 资源信息
	 */
	public static Properties load(String path) {
		assert path != null && !path.trim().equals("");

		Properties properties = new Properties();
		FileInputStream fin = null;
		path = getAbsolutePath(path);

		try {
			fin = new FileInputStream(path);
			properties.load(fin);

			return properties;
		} catch (IOException e) {
			LOGGER.error("加载properties文件失败", e);
			return null;
		} finally {
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException e) {
				LOGGER.error("关闭输入流失败", e);
			}
		}
	}

	/**
	 * 获取poroperties文件的内容
	 * 
	 * @param filePath
	 *            为空，就用默认的common.properties文件
	 * @param key
	 *            键
	 * @return
	 */
	public static String getValue(String filePath, String key) {

		return load(StringUtils.isBlank(filePath) ? defaultPath : filePath)
				.getProperty(key);
	}

	/**
	 * 拷贝文件
	 * 
	 * @param from
	 *            源文件
	 * @param to
	 *            拷贝地址
	 * @return boolean 拷贝是否成功
	 */
	public static boolean copy(String from, String to) {
		assert from != null && !from.trim().equals("") && to != null
				&& !to.trim().equals("");

		from = getAbsolutePath(from);
		to = getAbsolutePath(to);

		if (!isExists(from)) {
			return false;
		} else {
			FileChannel base = null;
			FileChannel copyto = null;
			try {
				base = new FileInputStream(from).getChannel();
				copyto = new FileOutputStream(to).getChannel();

				copyto.transferFrom(base, 0, base.size());

				base.close();
				copyto.close();
			} catch (IOException e) {
				LOGGER.error("拷贝文件失败", e);
				return false;
			} finally {
				if (base != null) {
					try {
						base.close();
					} catch (IOException e) {
						LOGGER.error("关闭输入流失败", e);
					}
				}

				if (copyto != null) {
					try {
						copyto.close();
					} catch (IOException e) {
						LOGGER.error("关闭输出流失败", e);
					}
				}
			}
			return true;
		}
	}

	/**
	 * 拷贝目录
	 * 
	 * @param from
	 *            源目录
	 * @param to
	 *            目标目录
	 * @return boolean 拷贝是否成功
	 */
	public static boolean copyDir(String from, String to) {
		assert from != null && !from.trim().equals("") && to != null
				&& !to.trim().equals("");

		from = getAbsolutePath(from);
		to = getAbsolutePath(to);

		if (!to.endsWith("/")) {
			to += "/";
		}

		if (!isExists(from)) {
			return false;
		} else {
			mkdir(to);
			File base = new File(from);

			if (base.isDirectory()) {
				File[] files = base.listFiles();
				for (File file : files) {
					if (file.isDirectory()) {
						copyDir(file.getAbsolutePath(), to + file.getName());
					} else {
						copy(file.getAbsolutePath(), to + file.getName());
					}
				}
				return true;
			} else {
				return copy(from, to);
			}
		}
	}

	/**
	 * 将文件移动到指定位置(不是renameTo实现 谨慎使用)
	 * 
	 * @param from
	 *            源目录
	 * @param to
	 *            目标目录
	 * @return boolean 移动是否成功
	 */
	public static boolean move(String from, String to) {
		assert from != null && !from.trim().equals("") && to != null
				&& !to.trim().equals("");

		return copyDir(from, to) && delete(from);
	}

	/**
	 * 将字符序列写到指定文件里
	 * 
	 * @param sequence
	 *            字符序列
	 * @param to
	 *            指定文件
	 * @return boolean 是否输出成功
	 */
	public static boolean write(CharSequence sequence, String to) {
		return write(sequence, to, false);
	}

	/**
	 * 将字符序列写到指定文件里
	 * 
	 * @param sequence
	 *            字符序列
	 * @param to
	 *            指定文件
	 * @param append
	 *            是否追加内容
	 * @return boolean 是否输出成功
	 */
	public static boolean write(CharSequence sequence, String to, boolean append) {
		assert sequence != null && !sequence.toString().trim().equals("")
				&& to != null && !to.trim().equals("");

		to = getAbsolutePath(to);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(to, append));
			writer.write(sequence.toString());
			writer.flush();

			return true;
		} catch (Exception e) {
			LOGGER.error("字符内容输出失败", e);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流失败", e);
				}
			}
		}
	}

	/**
	 * 将字符序列集合写到指定文件里
	 * 
	 * @param <T>
	 *            任何继承java.util.Collection类的子类
	 * 
	 * @param sequences
	 *            字符序列集合
	 * @param to
	 *            指定文件
	 * @return boolean 是否输出成功
	 */
	public static <T extends Collection<? extends CharSequence>> boolean write(
			T sequences, String to) {
		return write(sequences, to, false);
	}

	/**
	 * 将字符序列集合写到指定文件里
	 * 
	 * @param <T>
	 *            任何继承java.util.Collection类的子类
	 * 
	 * @param sequences
	 *            字符序列集合
	 * @param to
	 *            指定文件
	 * @param append
	 *            是否追加内容
	 * @return boolean 是否输出成功
	 */
	public static <T extends Collection<? extends CharSequence>> boolean write(
			T sequences, String to, boolean append) {
		assert sequences != null;

		to = getAbsolutePath(to);
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(to, append));
			for (CharSequence sequence : sequences) {
				writer.write(sequence.toString());
				writer.write("\r\n");
			}
			writer.flush();
			return true;
		} catch (Exception e) {
			LOGGER.error("字符内容输出失败", e);
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流失败", e);
				}
			}
		}
	}

	/**
	 * 读取指定文件中的字节 如果无法找到指定文件则返回null
	 * 
	 * @param from
	 *            指定文件
	 * @return java.io.ByteArrayOutputStream Java内存输出流
	 */
	public static ByteArrayOutputStream readBytes(String from) {
		assert from != null && !from.trim().equals("");

		from = getAbsolutePath(from);
		if (isExists(from)) {
			FileInputStream base = null;
			ByteArrayOutputStream out = null;

			try {
				base = new FileInputStream(from);
				out = new ByteArrayOutputStream();

				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;

				while ((count = base.read(buffer)) != -1) {
					out.write(buffer, 0, count);
				}

				base.close();

				return out;
			} catch (IOException e) {
				LOGGER.error("从字节流中读取内容失败", e);
				return null;
			} finally {
				if (base != null) {
					try {
						base.close();
					} catch (IOException e) {
						LOGGER.error("关闭输入流失败", e);
					}
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * 读取指定文件中的字符串 如果无法找到指定文件则返回null
	 * 
	 * @param from
	 *            指定文件
	 * @return java.lang.String 文件字符串
	 */
	public static String readString(String from) {
		assert from != null && !from.trim().equals("");

		from = getAbsolutePath(from);

		if (isExists(from)) {
			BufferedReader reader = null;
			StringBuilder builder = null;

			try {
				reader = new BufferedReader(new FileReader(from));
				builder = new StringBuilder();

				String line = null;
				boolean isStart = true;
				while ((line = reader.readLine()) != null) {
					if (!isStart) {
						builder.append("\r\n");
					} else {
						isStart = false;
					}
					builder.append(line);
				}

				return builder.toString();
			} catch (IOException e) {
				LOGGER.error("从字符流中读取内容失败", e);
				return null;
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						LOGGER.error("关闭输入流失败", e);
					}
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * 读取指定文件中的字符串 如果无法找到指定文件则返回null
	 * 
	 * @param in
	 *            文件流
	 * @return List<Map<String,String>>
	 */
	public static List<Map<String, String>> readMap(InputStream in) {
		return readMap(in, null);
	}

	public static List<Map<String, String>> readMap(InputStream in,
	                                                String splitFlag) {

		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		BufferedReader reader = null;
		String[] keys = null;
		String[] values = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = null;
			boolean isStart = true;
			while ((line = reader.readLine()) != null) {
				if (!isStart) {
					Map<String, String> map = new HashMap<String, String>();
					if (splitFlag == null) {
						values = line.split("	");
					} else {
						values = line.split(splitFlag);
					}

					for (int i = 0; i < values.length; i++) {
						map.put(keys[i].replaceAll("\"", ""),
								values[i].replaceAll("\"", ""));
					}
					retList.add(map);
				} else {
					if (splitFlag == null) {
						keys = line.split("	");
					} else {
						keys = line.split(splitFlag);
					}

					isStart = false;
				}
			}
		} catch (IOException e) {
			LOGGER.error("从字符流中读取内容失败", e);
			retList = null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOGGER.error("关闭输入流失败", e);
				}
			}
		}
		return retList;
	}

	// public static <T> T[] readClass(InputStream in, Class class1) {
	//
	// List<Map<String,String>> retList = new ArrayList<Map<String,String>>();
	// BufferedReader reader = null;
	// String[] keys = null;
	// String[] values = null;
	// try {
	// Object obj = class1.newInstance();
	// Field[] fields = class1.getDeclaredFields();
	// reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
	// String line = null;
	// boolean isStart = true;
	// while ((line = reader.readLine()) != null) {
	// if (!isStart) {
	// Map<String,String> map = new HashMap<String, String>();
	// values = line.split("	");
	// for (int i = 0; i < values.length; i++) {
	// map.put(keys[i],values[i]);
	// }
	// retList.add(map);
	//
	// for (int j = 0; j < fields.length; j++) {
	//
	// if(fields[j].equals(obj))
	// fields[j].setAccessible(true);
	//
	// if
	// (fields[j].getGenericType().toString().equals("class java.lang.Integer")
	// ){
	//
	// // Method m = obj.getMethod("get" + name);
	// // String value = (String) m.invoke(model); // 调用getter方法获取属性值
	// // if (value == null) {
	// // m = model.getClass().getMethod("set" + name, String.class);
	// // m.invoke(model, "");
	// // }
	// fields[j].set(obj, "");
	// }else
	// if(fields[j].getGenericType().toString().equals("class java.lang.String")
	// ){
	// fields[j].set(obj, "");
	// }else
	// if(fields[j].getGenericType().toString().equals("class java.lang.Float")
	// ){
	// fields[j].set(obj, "");
	// }else
	// if(fields[j].getGenericType().toString().equals("class java.lang.Long")
	// ){
	// fields[j].set(obj, "");
	// }else
	// if(fields[j].getGenericType().toString().equals("class java.lang.Double")
	// ){
	// fields[j].set(obj, "");
	// }else
	// if(fields[j].getGenericType().toString().equals("class java.lang.Double")
	// ){
	// fields[j].set(obj, "");
	// }
	// }
	// } else {
	// keys = line.split("	");
	// isStart = false;
	// }
	// }
	// } catch (IOException e) {
	// LOGGER.error("从字符流中读取内容失败", e);
	// retList = null;
	// }catch (InstantiationException e) {
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// e.printStackTrace();
	// } catch (NoSuchFieldException e) {
	// e.printStackTrace();
	// } catch (SecurityException e) {
	// e.printStackTrace() ;
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// } finally {
	// if (reader != null) {
	// try {
	// reader.close();
	// } catch (IOException e) {
	// LOGGER.error("关闭输入流失败", e);
	// }
	// }
	// }
	// return retList;
	// }

	public static List<Map<String, String>> readCsv(InputStream in) {
		CsvReader csv = new CsvReader(in, Charset.forName("UTF-8"));
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		String[] keys = null;
		String[] values = null;
		try {
			boolean isStart = true;
			while (csv.readRecord()) {
				if (!isStart) {
					Map<String, String> map = new HashMap<String, String>();
					values = csv.getValues();

					for (int i = 0; i < values.length; i++) {
						map.put(keys[i].replaceAll("\"", ""),
								values[i].replaceAll("\"", ""));
					}
					retList.add(map);
				} else {
					keys = csv.getValues();

					isStart = false;
				}
			}
		} catch (IOException e) {
			LOGGER.error("从字符流中读取内容失败", e);
			retList = null;
		} 
		return retList;

	}


	public static List<Map<String, String>> readCsv2(InputStream in) {
		CsvReader csv = new CsvReader(in, Charset.forName("UTF-8"));
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		String[] keys = null;
		String[] values = null;
		try {
			boolean isStart = true;
			while (csv.readRecord()) {
				if (!isStart) {
					Map<String, String> map = new HashMap<String, String>();
					values = csv.getValues();

					for (int i = 0; i < values.length; i++) {
						map.put(keys[i].replaceAll("\"", ""),
								values[i].replaceAll("\"", ""));
					}
					retList.add(map);
				} else {
					keys = csv.getValues();

					isStart = false;
				}
			}
		} catch (IOException e) {
			LOGGER.error("从字符流中读取内容失败", e);
			retList = null;
		}
		return retList;

	}

	/**
	 * 读取指定文件中的字符串 如果无法找到指定文件则返回null
	 * 
	 * @param from
	 *            指定文件
	 * @param checker
	 *            单行数据校验器
	 * @return java.lang.String 文件字符串
	 */
	public static List<String> readString(String from, LineChecker checker) {
		assert from != null && !from.trim().equals("");

		from = getAbsolutePath(from);
		if (isExists(from)) {
			BufferedReader reader = null;
			List<String> result = null;

			try {
				reader = new BufferedReader(new FileReader(from));
				result = new LinkedList<String>();

				String line = null;

				if (checker == null) {
					while ((line = reader.readLine()) != null) {
						result.add(line);
					}
				} else {
					while ((line = reader.readLine()) != null) {
						if (checker.check(line)) {
							result.add(checker.format(line));
						}
					}
				}

				return result;
			} catch (IOException e) {
				LOGGER.error("从字符流中读取内容失败", e);
				return null;
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						LOGGER.error("关闭输入流失败", e);
					}
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param path
	 *            指定目录
	 * @return boolean 创建目录是否成功
	 */
	public static boolean mkdir(String path) {
		assert path != null && !path.trim().equals("");

		return new File(getAbsolutePath(path)).mkdirs();
	}

	/**
	 * 删除指定文件
	 * 
	 * @param path
	 *            路径
	 * @return 是否删除
	 */
	public static boolean delete(String path) {
		if (isExists(path)) {
			File toDel = new File(getAbsolutePath(path));
			if (toDel.isDirectory()) {
				File[] files = toDel.listFiles();
				for (File f : files) {
					delete(f.getAbsolutePath());
				}
			}
			return toDel.delete();
		} else {
			return false;
		}
	}

	/**
	 * 判断指定文件是否存在
	 * 
	 * @param path
	 *            文件所在路径
	 * 
	 * @return boolean 判断结果
	 */
	public static boolean isExists(String path) {
		assert path != null && !path.trim().equals("");

		return new File(getAbsolutePath(path)).exists();
	}

	/**
	 * 获得绝对路径
	 * 
	 * @param path
	 *            路径
	 * @return 绝对路径
	 */
	public static String getAbsolutePath(String path) {
		if (path.startsWith("!")) {
			URL u = Thread.currentThread().getContextClassLoader()
					.getResource(EMPTY_STR);
			if (u != null) {
				path = u.getPath() + path.substring(1);
			} else {
				return null;
			}
		}

		return path;
	}

	/**
	 * 获得文件后缀名称
	 * 
	 * @param file
	 *            指定文件
	 * @return {@link String}
	 */
	public static String getFileSuffix(File file) {
		String fileName = file.getAbsolutePath();
		int lastIndex = fileName.lastIndexOf(".");

		if (lastIndex == -1) {
			return "";
		} else {
			return fileName.substring(lastIndex + 1);
		}
	}

	/**
	 * 自定义的字符串处理器
	 * 
	 * @author zhaojp
	 */
	public abstract static class LineChecker {
		/**
		 * 检查指定的行是否符合要求 如果符合要求则会作为结果返回 如果不符合则不添加到结果列表中
		 * 
		 * @param line
		 *            单行
		 * @return boolean 校验结果
		 */
		public boolean check(String line) {
			return true;
		}

		/**
		 * 将原始内容处理
		 * 
		 * @param line
		 *            单行
		 * @return java.lang.String 处理后的内容
		 */
		public String format(String line) {
			return line;
		}
	}

	// 常量属性

	/** 日志对象 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileUtils.class);

	/** 空字符串 */
	private static final String EMPTY_STR = "";

	/** 缓存大小 */
	private static final int BUFFER_SIZE = 2048;
}
