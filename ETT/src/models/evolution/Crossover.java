package models.evolution;

import engine.models.ICrossoverData;
import exception.ValidationException;
import models.LessonSortType;
import schema.models.ETTCrossover;

import java.io.Serializable;

public class Crossover implements ICrossoverData , Serializable
{

    private LessonSortType name;
    private int cuttingPoints;
    private CrossoverConfigurationType configuration;

    public Crossover(){

    }
    public Crossover(ETTCrossover ettCrossover) throws ValidationException {
        setName(ettCrossover);
        setCuttingPoints(ettCrossover);
        setConfiguration(ettCrossover);
    }

    public LessonSortType getName() {
        return name;
    }

    public void setName(ETTCrossover ettCrossover) throws ValidationException {
        setName(ettCrossover.getName());
    }

    public void setName(String name) throws ValidationException{
        this.name = LessonSortType.valueOfLabel(name);
        if(this.name == null){
            throw new ValidationException("Invalid crossover name: " + name);
        }
    }

    public int getCuttingPoints() {
        return cuttingPoints;
    }

    @Override
    public String getSortOperator() {
        return name.toString();
    }


    public void setCuttingPoints(ETTCrossover ettCrossover) throws ValidationException {
        setCuttingPoints(ettCrossover.getCuttingPoints());
    }

    public void setCuttingPoints(int cuttingPoints) throws ValidationException {
        this.cuttingPoints = cuttingPoints;
        if(this.cuttingPoints < 1){
            throw new ValidationException("Crossover cutting points can't be lower then 1");
        }
    }

    public String getConfiguration() {
        if(configuration==null)
            return "";
        return configuration.toString();
    }

    public void setConfiguration(ETTCrossover ettCrossover) throws ValidationException {
        String configuration = null;
        if(ettCrossover.getConfiguration() != null && ettCrossover.getConfiguration().contains("Orientation=")) {
            configuration = ettCrossover.getConfiguration().replace("Orientation=","");
        }
        setConfiguration(configuration);

    }

    public void setConfiguration(String configuration) throws ValidationException {
        if(name == LessonSortType.AspectOriented) {
            CrossoverConfigurationType config = CrossoverConfigurationType.valueOfLabel(configuration);
            if(config != null) {
                this.configuration = config;
            }else{
                throw new ValidationException("Invalid crossover configuration");
            }
        }
    }
}
