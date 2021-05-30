/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author user
 */
public class Token
{
    public String tokenValue;
    public String tokenType;
    public Token(String tokenValue, String tokenType)
    {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
    }
    public String getValue(){
        return this.tokenValue;
    }
    public String getType(){
        return this.tokenType;
    }
    String printToken() {
    return tokenValue +" is type of "+tokenType;
    }
}


enum TokenType{
    NUMBER,
    IDENTIFIER,
    OPERATOR,
    COMMENT,
    ASSIGNMENT,
    RESERVED_WORD
        }