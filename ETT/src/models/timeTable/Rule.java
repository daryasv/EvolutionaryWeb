package models.timeTable;

import engine.models.IRule;
import exception.ValidationException;
import schema.models.ETTRule;

import java.io.Serializable;

public class Rule implements IRule, Serializable {

    protected RuleId id;
    protected String configuration;
    protected RuleType type;

    public Rule(ETTRule ettRule) throws ValidationException {
      setId(ettRule.getETTRuleId());
      setConfiguration(ettRule.getETTConfiguration());
      setRuleType(ettRule.getType());
    }

    @Override
    public boolean isHard() {
        return type == RuleType.HARD;
    }

    @Override
    public String getName() {
        return this.id.toString();
    }

    public RuleId getId() {
        return id;
    }

    public void setId(RuleId id) throws ValidationException {
        if(id == null){
            throw new ValidationException("Invalid rule id");
        }
        this.id = id;
    }

    public void setId(String id) throws ValidationException {
        RuleId ruleId = RuleId.valueOfLabel(id);
        setId(ruleId);
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) throws ValidationException {
        if(type == null){
            throw new ValidationException("Invalid rule type");
        }
        this.type = type;
    }

    public void setRuleType(String ruleType) throws ValidationException {
        RuleType type = RuleType.valueOfLabel(ruleType);
        setType(type);
    }
}

