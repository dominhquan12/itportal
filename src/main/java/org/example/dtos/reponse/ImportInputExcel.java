package org.example.dtos.reponse;


import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class ImportInputExcel {
    public Integer rowId;
    public Set<String> result = new TreeSet<>();

    public boolean isError() {
        return !result.isEmpty();
    }

    public boolean isNotError() {
        return !isError();
    }

    public void appendResult(String messageError) {
        this.result.add(messageError);
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public Set<String> getResult() {
        return result;
    }

    public void setResult(Set<String> result) {
        this.result = result;
    }

    public String getResultText() {
        return String.join(", ", this.getResult());
    }

    public void setResultText(String resultText) {
        if (resultText != null) {
            Set<String> set = new TreeSet<>(Arrays.asList(resultText.split(", ")));
            this.result = set;
        }
    }
}
