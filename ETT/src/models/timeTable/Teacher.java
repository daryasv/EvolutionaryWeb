package models.timeTable;

import exception.ValidationException;
import schema.models.ETTTeacher;
import schema.models.ETTTeaches;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends Component{

    private List<Integer> subjectsIdsList;

    public Teacher(ETTTeacher ettTeacher) throws ValidationException {
        setId(ettTeacher.getId());
        setName(ettTeacher.getETTName());
        setSubjectsIdsList(ettTeacher);
    }

    public List<Integer> getSubjectsIdsList() {
        return subjectsIdsList;
    }

    //TODO : validation
    public void setSubjectsIdsList(ETTTeacher ettTeacher) {
        this.subjectsIdsList = new ArrayList<Integer>();
        //List<ETTTeaches> ettSubj = ettTeacher.getETTTeaching().getETTTeaches();
        //ettTeacher.getETTTeaching().getETTTeaches().forEach(subject -> this.subjectsIdsList.add(subject.getSubjectId()));
        for (ETTTeaches ettS : ettTeacher.getETTTeaching().getETTTeaches())
        {
            int subjectId =ettS.getSubjectId();
            this.subjectsIdsList.add(subjectId);

        }

    }
}
