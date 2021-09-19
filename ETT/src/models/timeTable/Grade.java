package models.timeTable;

import exception.ValidationException;
import schema.models.ETTClass;
import schema.models.ETTRequirements;
import schema.models.ETTStudy;

import java.util.HashMap;

public class Grade extends Component {

    private HashMap<Integer, Integer> requirements;

    public Grade(ETTClass ettClass) throws ValidationException {
        setId(ettClass.getId());
        setName(ettClass.getETTName());
        setRequirements(ettClass.getETTRequirements());
    }

    public HashMap<Integer, Integer> getRequirements() {
        return requirements;
    }

    public void setRequirements(HashMap<Integer, Integer> requirements) throws ValidationException {
        if(requirements != null) {
            this.requirements = requirements;
        }else {
            throw new ValidationException("Invalid class requirements");
        }
    }

    public void setRequirements(ETTRequirements ettRequirements) throws ValidationException {
        requirements = new HashMap<>();
        for (ETTStudy study : ettRequirements.getETTStudy()) {
            if(study.getSubjectId() < 0 ){
                throw new ValidationException("invalid class requirement subject id");
            }
            if(study.getHours() < 0){
                throw new ValidationException("invalid class requirement subject hours");
            }
            requirements.put(study.getSubjectId(), study.getHours());
        }
    }
}
