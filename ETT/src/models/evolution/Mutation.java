package models.evolution;

import engine.models.IMutation;
import exception.ValidationException;
import models.Lesson;
import models.Utils;
import schema.models.ETTMutation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mutation implements IMutation<Lesson> , Serializable
{
    private final List<Character> ALLOWED_COMPONENTS = new ArrayList<>(Arrays.asList('C','S','T','H','D'));

    private String name;
    private double probability;
    private int maxTupples;
    private char component;

    public static enum MutationOperators{
        FLIP_OPERATOR("Flipping"),
        SIZE_OPERATOR("Sizer");
        String operator;

        public String getOperatorName() {
            return operator;
        }

        MutationOperators(String operator){
            this.operator=operator;
        }

        public static MutationOperators valueOfLabel(String label) {
            for (MutationOperators e : values()) {
                if (e.operator.equals(label)) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return operator;
        }
    }

    public Mutation(String name, String probability, String maxTupples,String component) throws ValidationException {
        setName(name);
        setProbability(probability);
        setMaxTupples(maxTupples);
        setComponent(component);
    }

    public Mutation(ETTMutation ettMutation) throws ValidationException {
        setName(ettMutation);
        setProbability(ettMutation);
        setMaxTupples(ettMutation);
        setComponent(ettMutation);
    }

    public String getName() {
        return name;
    }

    public void setName(ETTMutation ettMutation) throws ValidationException {
       setName(ettMutation.getName());
    }

    public void setName(String name) throws ValidationException {
        if(name==null || name.isEmpty()){
            throw new ValidationException("Mutation name can't be empty");
        }
        this.name = name;
    }

    @Override
    public double getProbability() {
        return probability;
    }

    public void setProbability(String probability) throws ValidationException{
        try{
            double newProbability = Double.parseDouble(probability);
            if(newProbability>100 || newProbability==0 || newProbability< 0){
                throw new ValidationException("Invalid Probability");
            }
            this.probability = newProbability / 100;
        }catch (NumberFormatException exception){
            throw new ValidationException("Invalid Probability");
        }
    }

    public void setProbability(ETTMutation ettMutation) throws ValidationException {
        setProbability(ettMutation.getProbability());
    }

    public void setProbability(double probability) throws ValidationException{
        if(probability > 0) {
            this.probability = probability;
        }else{
            throw new ValidationException("Probability cant be under 0");
        }
    }

    public int getMaxTupples() {
        return maxTupples;
    }

    public void setMaxTupples(ETTMutation ettMutation) throws ValidationException {
        if(ettMutation.getConfiguration().isEmpty()){
            throw new ValidationException("Mutation configuration cannot be null");
        }
        if(ettMutation.getConfiguration().split(",")[0].split("=").length != 2){
            throw new ValidationException("Invalid Tupple mutation configuration");
        }

        String tuppleType = ettMutation.getConfiguration().split(",")[0].split("=")[0];
        if(!tuppleType.equals("TotalTupples") && !tuppleType.equals("MaxTupples")){
            throw new ValidationException("Tupple type - " + tuppleType + " - does not exists");
        }
        Integer tupple = Utils.tryParse(ettMutation.getConfiguration().split(",")[0].split("=")[1]);
        if(tupple ==null) {
            throw new ValidationException("Invalid tupple type " + ettMutation.getConfiguration().split(",")[0].split("=")[1]);
        }
        this.maxTupples = tupple;
    }

    public void setMaxTupples(String maxTupples) throws ValidationException {
        try {
            int tupples = Integer.parseInt(maxTupples);
            if(tupples <= 0){
                throw new ValidationException("invalid max tupples");
            }
            this.maxTupples = tupples;
        }catch (NumberFormatException e){
            throw new ValidationException("Invalid max tupples");
        }
    }

    public char getComponent() {
        return component;
    }

    public void setComponent(ETTMutation ettMutation) throws ValidationException {
        String[] configs = ettMutation.getConfiguration().split(",");
        if(configs.length > 1){
            this.component = ettMutation.getConfiguration().charAt(ettMutation.getConfiguration().length()-1);
            if(!ALLOWED_COMPONENTS.contains(component)) {
                throw new ValidationException("Invalid mutation component - " + this.component);
            }
        }
    }

    public void setComponent(String component) throws ValidationException {
        if(!this.name.equals("Flipping")){
            return;
        }
        if(component.length() == 1){
            char newComponent = component.charAt(0);
            if(!ALLOWED_COMPONENTS.contains(newComponent)) {
                throw new ValidationException("Invalid mutation component - " + newComponent);
            }else{
                this.component = newComponent;
            }
        }else{
            throw  new ValidationException("Invalid mutation component - " + this.component);
        }
    }

}
