package com.ggs.project;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Starbug
 * @Date 2020/9/4 19:11
 */
public class CodeGenerator {


    @Test
    public void generator(){
        //生成代码生成器对象
        AutoGenerator autoGenerator = new AutoGenerator();

        //设置模板引擎
        autoGenerator.setTemplateEngine(new VelocityTemplateEngine());

        //1.全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        System.out.println("projectPath = " + projectPath);
        globalConfig.setOutputDir(projectPath+"/src/main/java");
        globalConfig.setAuthor("Starbug");
        globalConfig.setOpen(false);//是否打开资源管理器
        globalConfig.setSwagger2(false);//实体类属性添加Swagger2注解
        globalConfig.setFileOverride(false);//是否覆盖
        globalConfig.setServiceName("%sService");//去除掉Service的I前缀
        globalConfig.setIdType(IdType.UUID);//id生成策略
        autoGenerator.setGlobalConfig(globalConfig);

        //2.设置数据源
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/poem?serverTimezone=GMT%2B8&characterEncoding=UTF-8");
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("123456");
        dataSourceConfig.setDbType(DbType.MYSQL);
        autoGenerator.setDataSource(dataSourceConfig);

        //3.包的设置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setModuleName("project");
        packageConfig.setParent("com.ggs");
        packageConfig.setEntity("entity");
        packageConfig.setService("service");
        packageConfig.setController("controller");
        packageConfig.setMapper("mapper");
        autoGenerator.setPackageInfo(packageConfig);

        //自定义输出配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {

            }
        };
        String templatePath = "/templates/mapper.xml.vm";
        List<FileOutConfig> focList = new ArrayList();
        FileOutConfig fileOutConfig = new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        };
        focList.add(fileOutConfig);
        cfg.setFileOutConfigList(focList);
        autoGenerator.setCfg(cfg);


        //4.策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setInclude("t_category","t_poem"); //指定要映射的表名字
        strategyConfig.setNaming(NamingStrategy.underline_to_camel); //包命名规则:驼峰命名
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);//列名称规则:驼峰命名
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setTablePrefix("t_");
        autoGenerator.setStrategy(strategyConfig);

        //执行
        autoGenerator.execute();


    }



}
