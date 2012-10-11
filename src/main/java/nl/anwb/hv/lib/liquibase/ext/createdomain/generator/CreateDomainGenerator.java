package nl.anwb.hv.lib.liquibase.ext.createdomain.generator;

import java.util.ArrayList;
import java.util.List;

import nl.anwb.hv.lib.liquibase.ext.createdomain.statement.CreateDomainStatement;
import liquibase.database.Database;
import liquibase.database.core.SybaseDatabase;
import liquibase.database.typeconversion.TypeConverterFactory;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;

public class CreateDomainGenerator extends AbstractSqlGenerator<CreateDomainStatement> {

    @Override
    public ValidationErrors validate(CreateDomainStatement createDomainStatement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();

        validationErrors.checkRequiredField("domainName", createDomainStatement.getDomainName());
        validationErrors.checkRequiredField("typeName", createDomainStatement.getDataType());

        if (createDomainStatement.isIfNotExists()) {
            //validationErrors.checkDisallowedField("ifNotExists", createDomainStatement.isIfNotExists(), database, HsqlDatabase.class, H2Database.class, DB2Database.class, CacheDatabase.class, MSSQLDatabase.class, DerbyDatabase.class, SybaseASADatabase.class, InformixDatabase.class);
        }

        return validationErrors;
    }

    @Override
    public Sql[] generateSql(CreateDomainStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        
        if (database instanceof SybaseDatabase) {
            return generateSqlSybase(statement, database, sqlGeneratorChain);
        }
        
        StringBuilder createBuilder = new StringBuilder();
        
        createBuilder.append("CREATE DOMAIN ");
        if (statement.isIfNotExists()) {
            createBuilder.append("IF NOT EXISTS ");
        }
        createBuilder.append(database.escapeDatabaseObject(statement.getDomainName()));
        createBuilder.append(" AS ");
        createBuilder.append(TypeConverterFactory.getInstance().findTypeConverter(database).getDataType(statement.getDataType(), false));
        
        Object defaultValue = statement.getDefaultValue(); 
        if (defaultValue != null) {
            createBuilder.append(" DEFAULT ");
            createBuilder.append(TypeConverterFactory.getInstance().findTypeConverter(database).getDataType(defaultValue).convertObjectToString(defaultValue, database));
        }
        if (!statement.isNullable()) {
            createBuilder.append(" NOT NULL");
        }
        String check = statement.getCheck();
        if (check != null) {
            createBuilder.append(" CHECK(");
            createBuilder.append(check);
            createBuilder.append(")");
        }
        return new Sql[] 
            {
                new UnparsedSql(createBuilder.toString())
            };
    }
    
    private Sql[] generateSqlSybase(CreateDomainStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        List<Sql> sql = new ArrayList<Sql>();
      
        boolean hasCheck = false;
        boolean hasDefault = false;
        
        if (statement.getCheck() != null && !statement.getCheck().isEmpty()) {
            hasCheck = true;
            
            StringBuilder ruleBuilder = new StringBuilder();
            
            ruleBuilder.append("CREATE RULE ");
            ruleBuilder.append("rl_");
            ruleBuilder.append(statement.getDomainName());
            ruleBuilder.append(" AS ");
            
            String rule = statement.getCheck().replaceAll("value", "@value");
            ruleBuilder.append(rule);
            
            sql.add(new UnparsedSql(ruleBuilder.toString()));
        }
        
        if (statement.getDefaultValue() != null) {
            hasDefault = true;
            Object defaultValue = statement.getDefaultValue(); 
            
            StringBuilder defaultBuilder = new StringBuilder();
            
            defaultBuilder.append("CREATE DEFAULT ");
            defaultBuilder.append("dlft_");
            defaultBuilder.append(statement.getDomainName());
            defaultBuilder.append(" AS ");
            defaultBuilder.append(TypeConverterFactory.getInstance().findTypeConverter(database).getDataType(defaultValue).convertObjectToString(defaultValue, database));
            
            sql.add(new UnparsedSql(defaultBuilder.toString()));
        }
        StringBuilder createBuilder = new StringBuilder();
        
        createBuilder.append("exec sp_addtype '");
        createBuilder.append(statement.getDomainName());
        createBuilder.append("', '");
        createBuilder.append(statement.getDataType());
        createBuilder.append("', ");
        createBuilder.append(statement.isNullable() ? "null" : "nonull");
        
        sql.add(new UnparsedSql(createBuilder.toString()));
        
        if (hasCheck) {
            StringBuilder bindBuilder = new StringBuilder();
            
            bindBuilder.append("sp_bindrule '");
            bindBuilder.append(statement.getSchemaName());
            bindBuilder.append(".rl_");
            bindBuilder.append(statement.getDomainName());
            bindBuilder.append("', '");
            bindBuilder.append(statement.getDomainName());
            bindBuilder.append("'");

            sql.add(new UnparsedSql(bindBuilder.toString()));
        }
        if (hasDefault) {
            StringBuilder bindBuilder = new StringBuilder();
            
            bindBuilder.append("sp_bindefault '");
            bindBuilder.append(statement.getSchemaName());
            bindBuilder.append(".dflt_");
            bindBuilder.append(statement.getDomainName());
            bindBuilder.append("', '");
            bindBuilder.append(statement.getDomainName());
            bindBuilder.append("'");

            sql.add(new UnparsedSql(bindBuilder.toString()));
        }
        
        return sql.toArray(new Sql[0]);
    }
}
