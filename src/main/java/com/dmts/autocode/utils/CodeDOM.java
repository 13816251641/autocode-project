package com.dmts.autocode.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 自动生成代码
 */
public class CodeDOM {

    /**
     * 构造参数，出入表名
     */
    public CodeDOM(String tableName) {
        this.tableName = tableName;
        // com.winning
        basePackage_ = "com\\winning\\springbootjpa\\";
//        package_ = basePackage_ + StringUtil.camelCaseName(tableName).toLowerCase() + "\\";
        package_ = basePackage_;
        //System.getProperty("user.dir") 获取的是项目所在路径，如果我们是子项目，则需要添加一层路径
        basePath = System.getProperty("user.dir") + "\\src\\main\\java\\" + package_;
        
//        basePath = "F:\\springbootjpagenerator" + "\\springboot-jpa\\src\\main\\java\\" + package_;
    }
    
    /**
     * /root/onlinejar/tmp_jars/jars_path
     * com.lujieni.cloud.service
     * 
     */
    public CodeDOM(String tableName,String workSpace,String inputBasePackage) {
    	this.tableName = tableName;
    	// com.winning
    	basePackage_ = inputBasePackage;
    	package_ = basePackage_+ File.separator;
    	basePath = workSpace + File.separator + package_ ;
    	
//        basePath = "F:\\springbootjpagenerator" + "\\springboot-jpa\\src\\main\\java\\" + package_;
    }

    /**
     * 数据连接相关
     */
    private static final String URL = "jdbc:sqlserver://172.16.6.164:1433;SelectMethod=cursor;DatabaseName=win60_dcs_test";
    private static final String USERNAME = "ods";
    private static final String PASSWORD = "@Welcome1";
    private static final String DRIVERCLASSNAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /**
     * 表名
     */
    private String tableName;

    /**
     * 基础路径
     */
    private String basePackage_;
    private String package_;
    private String basePath;

    /**
     * 创建pojo实体类
     */
    private void createEntity(List<TableInfo> tableInfos) {
        String mybasePath = basePath+".entities.";
        File file = FileUtil.createFile(mybasePath.replaceAll("\\.", Matcher.quoteReplacement(File.separator))  + StringUtil.camelCaseName(tableName) + ".java");
        StringBuffer stringBuffer = new StringBuffer();
        
        String replaceAll = package_.replaceAll("\\\\", ".");
        
        replaceAll = replaceAll.substring(0, replaceAll.lastIndexOf("."));

        replaceAll = "com.winning.dcg.event.collector.entity";
        
        stringBuffer.append(
                "package " + replaceAll + ";\n" +
                        "\n" +
                        "import lombok.Data;\n" +
                        "import javax.persistence.*;\n" +
                        "import java.io.Serializable;\n" +
                        "import java.util.Date;\n" +
                        "\n" +
                        "@Entity\n" +
                        "@Table(name = \"" + tableName + "\")\n" +
                        "@Data\n" +
                        "public class " + StringUtil.camelCaseName(tableName) + " implements Serializable {\n"
        );
        //遍历设置属性
        for (TableInfo tableInfo : tableInfos) {
            //主键
            if ("PRI".equals(tableInfo.getColumnKey())) {
                stringBuffer.append("    @Id\n");
            }else if ("0".equals(tableInfo.getColumnKey())){
            	stringBuffer.append("    @Id\n");
            }
            //自增
            if ("auto_increment".equals(tableInfo.getExtra())) {
                stringBuffer.append("    @GeneratedValue(strategy= GenerationType.IDENTITY)\n");
            }
            stringBuffer.append("    private " + StringUtil.typeMapping(tableInfo.getDataType()) + " " + StringUtil.camelCaseName(tableInfo.getColumnName()) + ";//" + tableInfo.getColumnComment() + "\n\n");
        }
        stringBuffer.append("}");
        FileUtil.fileWriter(file, stringBuffer);
    }

    /**
     * 创建vo类
     */
    private void createDTO(List<TableInfo> tableInfos) {
        File file = FileUtil.createFile(basePath + "dto\\" + StringUtil.camelCaseName(tableName) + "DTO.java");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(
                "package " + package_.replaceAll("\\\\", ".") + "dto;\n" +
                        "\n" +
//                        "import "+ basePackage_.replaceAll("\\\\", ".") +"common.pojo.PageCondition;"+
                        "import lombok.Data;\n" +
                        "import java.io.Serializable;\n" +
//                        "import java.util.Date;\n" +
                        "\n" +
                        "@Data\n" +
                        "public class " + StringUtil.camelCaseName(tableName) + "DTO implements Serializable {\n"
        );
        //遍历设置属性
        for (TableInfo tableInfo : tableInfos) {
            stringBuffer.append("    private " + StringUtil.typeMapping(tableInfo.getDataType()) + " " + StringUtil.camelCaseName(tableInfo.getColumnName()) + ";//" + tableInfo.getColumnComment() + "\n\n");
        }
        stringBuffer.append("}");
        FileUtil.fileWriter(file, stringBuffer);
    }
    
    // 创建 PageIn
    private void createPageIn(List<TableInfo> tableInfos) {
    	File file = FileUtil.createFile(basePath + "dto\\" + StringUtil.camelCaseName(tableName) + "PageIn.java");
    	StringBuffer stringBuffer = new StringBuffer();
    	stringBuffer.append(
    			"package " + package_.replaceAll("\\\\", ".") + "dto;\n" +
    					"\n" +
    					"import "+ basePackage_.replaceAll("\\\\", ".") +" common.pojo.PageCondition;"+
    					"import lombok.Data;\n" +
    					"import java.io.Serializable;\n" +
    					"import java.util.Date;\n" +
    					"\n" +
    					"@Data\n" +
    					"public class " + StringUtil.camelCaseName(tableName) + "PageIn extends PageCondition implements Serializable {\n"
    			);
    	//遍历设置属性
    	for (TableInfo tableInfo : tableInfos) {
    		stringBuffer.append("    private " + StringUtil.typeMapping(tableInfo.getDataType()) + " " + StringUtil.camelCaseName(tableInfo.getColumnName()) + ";//" + tableInfo.getColumnComment() + "\n\n");
    	}
    	stringBuffer.append("}");
    	FileUtil.fileWriter(file, stringBuffer);
    }

    /**
     * extends JpaRepository<OdsCdcTime,Long>
     * 创建repository类
     */
    private void createRepository(List<TableInfo> tableInfos) {
        String mybasePath=basePath+".repositories.";
        File file = FileUtil.createFile(mybasePath.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + StringUtil.camelCaseName(tableName) + "Repository.java");
        StringBuffer stringBuffer = new StringBuffer();
        String t = "String";
        //遍历属性
        for (TableInfo tableInfo : tableInfos) {
            //主键
            if ("PRI".equals(tableInfo.getColumnKey()) || "0".equals(tableInfo.getColumnKey())) {
                t = StringUtil.typeMapping(tableInfo.getDataType());
            }
        }
        
        String replaceAll = package_.replaceAll("\\\\", ".");
        
        replaceAll = replaceAll.substring(0, replaceAll.lastIndexOf("."));

        replaceAll = "com.winning.dcg.event.collector.repository";

        String packageName = "com.winning.dcg.event.collector.entity.";

        stringBuffer.append(
                "package " + replaceAll + ";\n" +
                        "\n" +
                        //"import " + basePackage_.replaceAll("\\\\", ".") + "common.repository.*;\n" +
                        //"import " + package_.replaceAll("\\\\", ".")  + StringUtil.camelCaseName(tableName) + ";\n" +
                        "import " + packageName + StringUtil.camelCaseName(tableName) + ";\n" +
                        "import org.springframework.stereotype.Repository;\n" +
                        "\n" +
                        "@Repository\n" +
                        "public interface " + StringUtil.camelCaseName(tableName) + "Repository extends JpaRepository<" + StringUtil.camelCaseName(tableName) + ", " + t + "> {"
        );
        stringBuffer.append("\n");
        stringBuffer.append("}");
        FileUtil.fileWriter(file, stringBuffer);
    }

    /**
     * 创建service类
     */
    private void createService(List<TableInfo> tableInfos) {
        File file = FileUtil.createFile(basePath + "service\\" + StringUtil.camelCaseName(tableName) + "Service.java");
        StringBuffer stringBuffer = new StringBuffer();
        String t = "String";
        //遍历属性
        for (TableInfo tableInfo : tableInfos) {
            //主键
            if ("PRI".equals(tableInfo.getColumnKey())) {
                t = StringUtil.typeMapping(tableInfo.getDataType());
            }
        }
        stringBuffer.append(
                "package " + package_.replaceAll("\\\\", ".") + "service;\n" +
                        "\n" +
                        "import " + basePackage_.replaceAll("\\\\", ".") + "common.service.*;\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "entity." + StringUtil.camelCaseName(tableName) + ";\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "dto." + StringUtil.camelCaseName(tableName) + "DTO;\n" +
                        "\n" +
                        "public interface " + StringUtil.camelCaseName(tableName) + "Service extends CommonService<" + StringUtil.camelCaseName(tableName) + "DTO, " + StringUtil.camelCaseName(tableName) + ", " + t + "> {"
        );
        stringBuffer.append("\n");
        stringBuffer.append("}");
        FileUtil.fileWriter(file, stringBuffer);

        //Impl
        File file1 = FileUtil.createFile(basePath + "service\\" + StringUtil.camelCaseName(tableName) + "ServiceImpl.java");
        StringBuffer stringBuffer1 = new StringBuffer();
        stringBuffer1.append(
                "package " + package_.replaceAll("\\\\", ".") + "service;\n" +
                        "\n" +
                        "import " + basePackage_.replaceAll("\\\\", ".") + "common.service.*;\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "entity." + StringUtil.camelCaseName(tableName) + ";\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "dto." + StringUtil.camelCaseName(tableName) + "DTO;\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "repository." + StringUtil.camelCaseName(tableName) + "Repository;\n" +
                        "import org.springframework.beans.factory.annotation.Autowired;\n" +
                        "import org.springframework.stereotype.Service;\n" +
                        "import org.springframework.transaction.annotation.Transactional;\n" +
                        "import javax.persistence.EntityManager;\n" +
                        "import javax.persistence.PersistenceContext;\n" +
                        "\n" +
                        "@Service\n" +
                        "@Transactional\n" +
                        "public class " + StringUtil.camelCaseName(tableName) + "ServiceImpl extends CommonServiceImpl<" + StringUtil.camelCaseName(tableName) + "DTO, " + StringUtil.camelCaseName(tableName) + ", " + t + "> implements " + StringUtil.camelCaseName(tableName) + "Service{"
        );
        stringBuffer1.append("\n\n");
        stringBuffer1.append(
                "    @PersistenceContext\n" +
                        "    private EntityManager em;\n");

        stringBuffer1.append("" +
                "    @Autowired\n" +
                "    private " + StringUtil.camelCaseName(tableName) + "Repository " + StringUtil.camelCaseName(tableName) + "Repository;\n");
        stringBuffer1.append("}");
        FileUtil.fileWriter(file1, stringBuffer1);
    }

    /**
     * 创建controller类
     */
    private void createController(List<TableInfo> tableInfos) {
        File file = FileUtil.createFile(basePath + "controller\\" + StringUtil.camelCaseName(tableName) + "Controller.java");
        StringBuffer stringBuffer = new StringBuffer();
        String t = "String";
        //遍历属性
        for (TableInfo tableInfo : tableInfos) {
            //主键
            if ("PRI".equals(tableInfo.getColumnKey())) {
                t = StringUtil.typeMapping(tableInfo.getDataType());
            }
        }
        stringBuffer.append(
                "package " + package_.replaceAll("\\\\", ".") + "controller;\n" +
                        "\n" +
                        "import " + basePackage_.replaceAll("\\\\", ".") + "common.controller.*;\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "entity." + StringUtil.camelCaseName(tableName) + ";\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "DTO." + StringUtil.camelCaseName(tableName) + "DTO;\n" +
                        "import " + package_.replaceAll("\\\\", ".") + "service." + StringUtil.camelCaseName(tableName) + "Service;\n" +
                        "import org.springframework.beans.factory.annotation.Autowired;\n" +
                        "import org.springframework.web.bind.annotation.*;\n" +
                        "\n" +
                        "@RestController\n" +
                        "@RequestMapping(\"/" + StringUtil.camelCaseName(tableName) + "/\")\n" +
                        "public class " + StringUtil.camelCaseName(tableName) + "Controller extends CommonController<" + StringUtil.camelCaseName(tableName) + "DTO, " + StringUtil.camelCaseName(tableName) + ", " + t + "> {"
        );
        stringBuffer.append("\n");
        stringBuffer.append("" +
                "    @Autowired\n" +
                "    private " + StringUtil.camelCaseName(tableName) + "Service " + StringUtil.camelCaseName(tableName) + "Service;\n");
        stringBuffer.append("}");
        FileUtil.fileWriter(file, stringBuffer);
    }

    /**
     * 获取表结构信息
     *
     * @return list
     */
    private List<TableInfo> getTableInfo() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<TableInfo> list = new ArrayList<>();
        try {
            conn = DBConnectionUtil.getConnection();
//            mysql方式
//            String sql = "select column_name,data_type,column_comment,column_key,extra from information_schema.columns where table_name=?";
            String sql = "SELECT syscolumns.name column_name,systypes.name data_type,	CASE WHEN syscolumns.name = (SELECT\r\n" + 
            		"	syscolumns.NAME \r\n" + 
            		"FROM\r\n" + 
            		"	syscolumns,\r\n" + 
            		"	sysobjects,\r\n" + 
            		"	sysindexes,\r\n" + 
            		"	sysindexkeys \r\n" + 
            		"WHERE\r\n" + 
            		"	syscolumns.id = object_id(?) \r\n" + 
            		"	AND sysobjects.xtype = 'PK' \r\n" + 
            		"	AND sysobjects.parent_obj = syscolumns.id \r\n" + 
            		"	AND sysindexes.id = syscolumns.id \r\n" + 
            		"	AND sysobjects.NAME = sysindexes.NAME \r\n" + 
            		"	AND sysindexkeys.id = syscolumns.id \r\n" + 
            		"	AND sysindexkeys.indid = sysindexes.indid \r\n" + 
            		"	AND syscolumns.colid = sysindexkeys.colid) THEN '0'\r\n" + 
            		"\r\n" + 
            		"	ELSE '1' END  column_key,\n" + 
            		"syscolumns.length \n" + 
            		"FROM syscolumns, systypes \n" + 
            		"WHERE syscolumns.xusertype = systypes.xusertype \n" + 
            		"AND syscolumns.id = object_id(?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableName);
            ps.setString(2, tableName);
            rs = ps.executeQuery();
            while (rs.next()) {
                TableInfo tableInfo = new TableInfo();
                //列名，全部转为小写
                tableInfo.setColumnName(rs.getString("column_name").toLowerCase());
                //列类型
                tableInfo.setDataType(rs.getString("data_type"));
                //列注释
                tableInfo.setColumnComment("");
                //主键
                tableInfo.setColumnKey(rs.getString("column_key"));
                //主键类型
//                tableInfo.setExtra(rs.getString("extra"));
                list.add(tableInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            assert rs != null;
            DBConnectionUtil.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * file工具类
     */
    private static class FileUtil {
        /**
         * 创建文件
         *
         * @param pathNameAndFileName 路径跟文件名
         * @return File对象
         */
        private static File createFile(String pathNameAndFileName) {
            File file = new File(pathNameAndFileName);
            try {
                //获取父目录
                File fileParent = file.getParentFile();
                if (!fileParent.exists()) {
                    fileParent.mkdirs();
                }
                //创建文件
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (Exception e) {
                file = null;
                System.err.println("新建文件操作出错");
                e.printStackTrace();
            }
            return file;
        }

        /**
         * 字符流写入文件
         *
         * @param file         file对象
         * @param stringBuffer 要写入的数据
         */
        private static void fileWriter(File file, StringBuffer stringBuffer) {
            //字符流
            try {
                FileWriter resultFile = new FileWriter(file, false);//true,则追加写入 false,则覆盖写入
                PrintWriter myFile = new PrintWriter(resultFile);
                //写入
                myFile.println(stringBuffer.toString());

                myFile.close();
                resultFile.close();
            } catch (Exception e) {
                System.err.println("写入操作出错");
                e.printStackTrace();
            }
        }
    }

    /**
     * 字符串处理工具类
     */
    private static class StringUtil {
        /**
         * 数据库类型->JAVA类型
         *
         * @param dbType 数据库类型
         * @return JAVA类型
         */
/*        private static String typeMapping(String dbType) {
            String javaType = "";
            if ("int|integer".contains(dbType)) {
                javaType = "Integer";
            } else if ("float|double|decimal|real".contains(dbType)) {
                javaType = "Double";
            } else if ("date|time|datetime|timestamp".contains(dbType)) {
                javaType = "Date";
            } else {
                javaType = "String";
            }
            return javaType;
        }*/
    	
        private static String typeMapping(String dbType) {
            String javaType = "";
            
            switch (dbType) {
			case "bigint":
				javaType = "Long";
				break;
			case "binary":
				javaType = "Byte[]";
				break;
			case "bit":
				javaType = "Boolean";
				break;
			case "char":
				javaType = "String";
				break;
			case "decimal":
				javaType = "java.math.BigDecimal";
				break;
			case "money":
				javaType = "java.math.BigDecimal";
				break;
			case "smallmoney":
				javaType = "java.math.BigDecimal";
				break;
			case "float":
				javaType = "Double";
				break;
			case "int":
				javaType = "Integer";
				break;
			case "text":
				javaType = "String";
				break;
			case "nchar":
				javaType = "String";
				break;
			case "nvarchar":
				javaType = "String";
				break;
			case "numeric":
				javaType = "Long";
				break;
			case "datetime":
				javaType = "java.util.Date";
				break;
			case "date":
				javaType = "java.util.Date";
				break;
			case "timestamp":
				javaType = "java.util.Date";
				break;
			case "varchar":
				javaType = "String";
				break;
			case "tinyint":
				javaType = "Short";
			case "smallint":
				javaType = "Short";
				break;

			default:
				break;
			}
            
            
            return javaType;
        }

        /**
         * 驼峰转换为下划线
         */
        public static String underscoreName(String camelCaseName) {
            StringBuilder result = new StringBuilder();
            if (camelCaseName != null && camelCaseName.length() > 0) {
                result.append(camelCaseName.substring(0, 1).toLowerCase());
                for (int i = 1; i < camelCaseName.length(); i++) {
                    char ch = camelCaseName.charAt(i);
                    if (Character.isUpperCase(ch)) {
                        result.append("_");
                        result.append(Character.toLowerCase(ch));
                    } else {
                        result.append(ch);
                    }
                }
            }
            return result.toString();
        }

        /**
         * 首字母大写
         */
/*        public static String captureName(String name) {
            char[] cs = name.toCharArray();
            cs[0] -= 32;
            return String.valueOf(cs);

        }*/

        /**
         * 下划线转换为驼峰
         */
/*        public static String camelCaseName(String underscoreName) {
            StringBuilder result = new StringBuilder();
            if (underscoreName != null && underscoreName.length() > 0) {
                boolean flag = false;
                for (int i = 0; i < underscoreName.length(); i++) {
                    char ch = underscoreName.charAt(i);
                    if ("_".charAt(0) == ch) {
                        flag = true;
                    } else {
                        if (flag) {
                            result.append(Character.toUpperCase(ch));
                            flag = false;
                        } else {
                            result.append(ch);
                        }
                    }
                }
            }
            return result.toString();
        }*/
        public static String camelCaseName(String line) {
        	if(line==null||"".equals(line)){
		   return "";
		  }
		  StringBuffer sb=new StringBuffer();
		  Pattern pattern=Pattern.compile("([A-Za-z\\d]+)(_)?");
		  Matcher matcher=pattern.matcher(line);
		  while(matcher.find()){
		   String word=matcher.group();
		   sb.append(false&&matcher.start()==0?Character.toLowerCase(word.charAt(0)):Character.toUpperCase(word.charAt(0)));
		   int index=word.lastIndexOf('_');
		   if(index>0){
		    sb.append(word.substring(1, index).toLowerCase());
		   }else{
		    sb.append(word.substring(1).toLowerCase());
		   }
		  }
		  return sb.toString();
        }
    }

    /**
     * JDBC连接数据库工具类
     */
    private static class DBConnectionUtil {

        {
            // 1、加载驱动
            try {
                Class.forName(DRIVERCLASSNAME);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 返回一个Connection连接
         *
         * @return
         */
        public static Connection getConnection() {
            Connection conn = null;
            // 2、连接数据库
            try {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return conn;
        }

        /**
         * 关闭Connection，Statement连接
         *
         * @param conn
         * @param stmt
         */
        public static void close(Connection conn, Statement stmt) {
            try {
                conn.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        /**
         * 关闭Connection，Statement，ResultSet连接
         *
         * @param conn
         * @param stmt
         * @param rs
         */
        public static void close(Connection conn, Statement stmt, ResultSet rs) {
            try {
                close(conn, stmt);
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 表结构行信息实体类
     */
    private class TableInfo {
        private String columnName;
        private String dataType;
        private String columnComment;
        private String columnKey;
        private String extra;

        TableInfo() {
        }

        String getColumnName() {
            return columnName;
        }

        void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        String getDataType() {
            return dataType;
        }

        void setDataType(String dataType) {
            this.dataType = dataType;
        }

        String getColumnComment() {
            return columnComment;
        }

        void setColumnComment(String columnComment) {
            this.columnComment = columnComment;
        }

        String getColumnKey() {
            return columnKey;
        }

        void setColumnKey(String columnKey) {
            this.columnKey = columnKey;
        }

        String getExtra() {
            return extra;
        }

        void setExtra(String extra) {
            this.extra = extra;
        }
    }

    /**
     * 快速创建，供外部调用，调用之前先设置一下项目的基础路径
     */
    public String create() {
        List<TableInfo> tableInfo = getTableInfo();
        createEntity(tableInfo);
        createRepository(tableInfo);
       /* createDTO(tableInfo);
        createPageIn(tableInfo);
        createRepository(tableInfo);
        createService(tableInfo);
        createController(tableInfo);*/
        
        System.out.println("生成路径位置：" + basePath.replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
        return tableName + " 后台代码生成完毕！";
    }

    public static void main(String[] args) {
/*        String[] tables = {"ONLINE_EVENT_CONFIG"};
        for (String table : tables) {
            String msg = new CodeDOM(table).create();
            System.out.println(msg);
        }*/
    	
//    	String msg = new CodeDOM("ODS_PET","C:\\Users\\JayZhou\\Desktop\\weining\\项目BACK\\","com.lujieni.cloud.service").create();
    	String msg = new CodeDOM("ODS_PET","/root/onlinejar/tmp_jars/jars_path","com.lujieni.cloud.service").create();
        System.out.println(msg);
    	
    	
    }
    
    
    
}
