package nl.anwb.hv.lib.liquibase.ext.createdomain.generator;

import java.util.ArrayList;
import java.util.List;

import liquibase.database.Database;
import liquibase.database.core.SybaseDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;

import nl.anwb.hv.lib.liquibase.ext.createdomain.statement.DropDomainStatement;

public class DropDomainGenerator extends AbstractSqlGenerator<DropDomainStatement> {

    @Override
    public ValidationErrors validate(DropDomainStatement DropDomainStatement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        ValidationErrors validationErrors = new ValidationErrors();

        validationErrors.checkRequiredField("domainName", DropDomainStatement.getDomainName());

        return validationErrors;
    }

    @Override
    public Sql[] generateSql(DropDomainStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {

        if (database instanceof SybaseDatabase) {
            return generateSqlSybase(statement, database, sqlGeneratorChain);
        }

        StringBuilder createBuilder = new StringBuilder();

        createBuilder.append("DROP DOMAIN ");
        createBuilder.append(database.escapeDatabaseObject(statement.getDomainName()));

        return new Sql[]
            {
                new UnparsedSql(createBuilder.toString())
            };
    }

    @SuppressWarnings("unused")
    private Sql[] generateSqlSybase(DropDomainStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        List<Sql> sql = new ArrayList<Sql>();

        StringBuilder createBuilder = new StringBuilder();

        createBuilder.append("exec sp_droptype '");
        createBuilder.append(statement.getDomainName());
        sql.add(new UnparsedSql(createBuilder.toString()));

        return sql.toArray(new Sql[0]);
    }
}
