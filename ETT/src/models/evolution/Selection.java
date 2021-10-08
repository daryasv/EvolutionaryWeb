package models.evolution;

import engine.models.ISelectionData;
import engine.models.SelectionType;
import exception.ValidationException;
import schema.models.ETTSelection;

import java.io.Serializable;

public class Selection implements ISelectionData , Serializable
{
    private SelectionType type;
    private double configuration;
    private int elitism;

    public Selection(String type,double value,int elitism) throws ValidationException {
        setType(type);
        setConfiguration(value);
        setElitism(elitism);
    }

    public Selection(ETTSelection ettSelection) throws ValidationException {
        setType(ettSelection.getType());
        setConfiguration(ettSelection.getConfiguration());
        setElitism(ettSelection.getETTElitism());
    }

    public void setElitism(Integer ettElitism) {
        if(ettElitism == null){
            elitism = 0;
        }else{
            elitism = ettElitism;
        }
    }

    public SelectionType getType() {
        return type;
    }

    public void setType(SelectionType type) throws ValidationException {
        if(type == null){
            throw new ValidationException("Invalid selection type");
        }
        this.type = type;
    }

    public void setType(String type) throws ValidationException {
        SelectionType selectionType = SelectionType.valueOfLabel(type);
        setType(selectionType);
    }

    @Override
    public double getConfiguration() {
        return configuration;
    }

    @Override
    public int getElitismCount() {
        return elitism;
    }

    public void setConfiguration(String configuration) throws ValidationException {
        if(configuration != null) {
            if (!configuration.contains("=")) {
                throw new ValidationException("Invalid selection config");
            }
            int value = Integer.parseInt(configuration.split("=")[1]);
            setConfiguration(value);
        }
    }

    public void setConfiguration(double value) throws ValidationException {
        if(type == SelectionType.Truncation){
            value = (int) value;
            if(value < 1 || value > 100){
                throw new ValidationException("Invalid selection config");
            }
        }
        else if(type ==SelectionType.Tournament){
            if(value < 0 || value > 1){
                throw new ValidationException("Invalid selection config");
            }
        }
        this.configuration = value;
    }
}
