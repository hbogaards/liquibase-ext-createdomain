package nl.anwb.hv.lib.liquibase.ext.createdomain.change;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import liquibase.change.DatabaseChange;
import nl.anwb.hv.lib.liquibase.ext.createdomain.statement.CreateDomainStatement;

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.database.Database;
import liquibase.statement.DatabaseFunction;
import liquibase.statement.SqlStatement;
import liquibase.util.ISODateFormat;
import liquibase.util.StringUtils;

@DatabaseChange(name="createDomain", description = "Create Domain", priority = ChangeMetaData.PRIORITY_DEFAULT)
public class CreateDomainChange extends AbstractChange {
    
    private String schemaName;
    private String domainName;
    private String dataType;
    private String check;
    private Boolean ifNotExists;
    private Boolean nullable = Boolean.TRUE;
    private String defaultValue;
    private String defaultValueNumeric;
    private String defaultValueDate;
    private Boolean defaultValueBoolean;
    private DatabaseFunction defaultValueComputed;

    public CreateDomainChange() {
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public Boolean getIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(Boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }
    
    public Boolean getNullable() {
        return nullable;
    }
    
    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public String getDefaultValueNumeric() {
        return defaultValueNumeric;
    }

    public void setDefaultValueNumeric(String defaultValueNumeric) {
        this.defaultValueNumeric = defaultValueNumeric;
    }

    public String getDefaultValueDate() {
        return defaultValueDate;
    }

    public void setDefaultValueDate(String defaultValueDate) {
        this.defaultValueDate = defaultValueDate;
    }


    public Boolean getDefaultValueBoolean() {
        return defaultValueBoolean;
    }

    public void setDefaultValueBoolean(Boolean defaultValueBoolean) {
        this.defaultValueBoolean = defaultValueBoolean;
    }

    public DatabaseFunction getDefaultValueComputed() {
        return defaultValueComputed;
    }

    public void setDefaultValueComputed(DatabaseFunction defaultValueComputed) {
        this.defaultValueComputed = defaultValueComputed;
    }

    public SqlStatement[] generateStatements(Database database) {
        List<SqlStatement> statements = new ArrayList<SqlStatement>();

        boolean ifNotExists = false;
        if (getIfNotExists() != null && getIfNotExists()) {
            ifNotExists = true;
        }
        boolean nullable = false;
        if (getNullable() != null && getNullable()) {
            nullable = true; 
        }

        statements.add(new CreateDomainStatement(
                getSchemaName() == null ? database.getDefaultSchemaName() : getSchemaName(), 
                getDomainName(), getDataType(), generateDefaultValue(), getCheck(),
                ifNotExists, nullable));

        return statements.toArray(new SqlStatement[statements.size()]);
    }

    public String getConfirmationMessage() {
        return "Domain " + getDomainName() + " created";
    }

    @Override
    protected Change[] createInverses() {
        DropDomainChange inverse = new DropDomainChange();
        inverse.setDomainName(getDomainName());
        inverse.setSchemaName(getSchemaName());

        return new Change[] { inverse };
    }
    
    private Object generateDefaultValue() {
        Object defaultVal = null;

        if (getDefaultValue() != null) {
            defaultVal = getDefaultValue();
        }
        else if (getDefaultValueBoolean() != null) {
            defaultVal = Boolean.valueOf(getDefaultValueBoolean());
        }
        else if (getDefaultValueNumeric() != null) {
            try {
                defaultVal = NumberFormat.getInstance(Locale.US).
                    parse(getDefaultValueNumeric()); 
            }
            catch (ParseException e) {
                defaultVal = new DatabaseFunction(getDefaultValueNumeric());
            }
        }
        else if (getDefaultValueDate() != null) {
            try {
                defaultVal = new ISODateFormat().parse(getDefaultValueDate());
            }
            catch (ParseException e) {
                defaultVal = new DatabaseFunction(getDefaultValueDate());
            }
        }
        else if (getDefaultValueComputed() != null) {
            defaultVal = getDefaultValueComputed();
        }
        
        return defaultVal;
    }
}
