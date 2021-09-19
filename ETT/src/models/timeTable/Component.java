package models.timeTable;

import exception.ValidationException;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Component implements Serializable {
   private int id;
   private String name;

   public int getId() {
      return id;
   }

   public void setId(int id) throws ValidationException {
      if(id > 0){
         this.id = id;
      }else{
         throw new ValidationException("Invalid id");
      }
   }

   public String getName() {
      return name;
   }

   public void setName(String name) throws ValidationException {
      if(name != null && name.length() > 1) {
         this.name = name;
      }else {
         throw new ValidationException("Invalid name");
      }
   }

   private boolean checkIfNext(Component obj){
      return this.id + 1 == obj.getId();
   }

   public static boolean checkIfRunningIds(List<Component> componentList) {
      componentList.sort(Comparator.comparingInt(Component::getId));
      for (int i = 0; i < componentList.size() - 1; i++) {
         if (!componentList.get(i).checkIfNext(componentList.get(i + 1))) {
            return false;
         }
      }
      return true;
   }
}
