package engine.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Solution<T> implements Serializable {

    private List<T> list;

    public Solution() {
        this.list = new ArrayList<>();
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void addItemToList(T item)
    {
        this.list.add(item);
    }

}
