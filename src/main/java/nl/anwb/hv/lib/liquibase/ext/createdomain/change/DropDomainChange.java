package nl.anwb.hv.lib.liquibase.ext.createdomain.change;

import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.util.StringUtils;
import nl.anwb.hv.lib.liquibase.ext.createdomain.statement.DropDomainStatement;

public class DropDomainChange extends AbstractChange {
    
    private String schemaName;
    private String domainName;

    public DropDomainChange() {
        super("dropDomain", "Drop Domain", ChangeMetaData.PRIORITY_DEFAULT);
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = StringUtils.trimToNull(schemaName);
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[]{
                new DropDomainStatement(
                    getSchemaName() == null ? database.getDefaultSchemaName()
                            : getSchemaName(), getDomainName())
        };
    }

    @Override
    public String getConfirmationMessage() {
        return "Domain " + getDomainName() + " dropped";
    }

}
