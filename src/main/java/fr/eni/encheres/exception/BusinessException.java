package fr.eni.encheres.exception;

import java.util.ArrayList;
import java.util.List;

public class BusinessException extends Exception {

    private static final long serialVersionUID = 1L;					
    private List<String> messages;

    public BusinessException() {
        super();
        this.messages = new ArrayList<>();
    }
    
    public BusinessException(String message) {
        super(message);
        this.messages = new ArrayList<>();
        this.messages.add(message);
    }

    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.messages = new ArrayList<>();
        this.messages.add(message);
      
    }

    public void add(String message) {
        this.messages.add(message);
    }

    public Iterable<String> getMessages(){
        return messages;
    }

    public boolean hasError() {
        return !this.messages.isEmpty();
    }
}
