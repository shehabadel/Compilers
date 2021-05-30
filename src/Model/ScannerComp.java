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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ScannerComp
{
    static public ArrayList<Character>  charArray = new ArrayList<Character>();
    static public String [] reservedWords = {"if","then","else","end","repeat","until","read","write"};
    static public String [] specialSymbols ={"+", "-", "*", "/", "=", "<", ">", "(", ")", ";","–","–"};
    static public HashMap<String, String> symbolsMap=new HashMap<>();
    static public HashMap<String, String> reservedMap=new HashMap<>();
    static public ArrayList<Token> tokenList = new ArrayList<Token>();

    static States currentState =States.START;
    static boolean nextState=false;
    
    public ScannerComp(){}
    
    public static void scan (String inputLine )
    {
       //adding white space in the end of each input line
       //in order to allow last character to be tokenized
       //with no errors at all. For example read x;
       // ; will not be tokenzied since it is in the last iteration
       // and we still have to process its state
        inputLine+=" ";
        
        //create hashMap for Symbols and reserved words
        initializeMap();
        
        //making sure there are no characters existing
        //from the previous inputs (related to GUI)
        charArray.clear();
        
        //created one variable that collects all characters
        //called token. created a boolean called nextState which we use
        //after DONE state
        //to set currentState based on the current currentCharacter
        // for example
        // x; --going to call ";" the foreign character--
        // x will be considered as an identifier in IDENTIFIER State
        //then when another character comes that does not match our identifier's regex
        //we will move to OTHER State where we will set nextState to true
        //and then move to DONE where we will tokenize "x" alone and then set current state
        //based on ";" type

        
        //initialzing variable to hold token characters 
        //before tokenizing.
        String token="";

        //adding each character from inputLine to a character array
        //so that it can be easier to iterate and process them.
        for (char c : inputLine.toCharArray())
        {
            charArray.add(c);
        }


        //iterating over each character inside characterArray
        for(int i=0; i<charArray.size(); i++) {

             
            String currentChar = charArray.get(i).toString();
            
                //Checking whether currentState is Start
                //According to TINY Language's DFA
                //the initial state is always START
                if(currentState==States.START) {
                    
                    //Incase there is a letter (a-z) or (A-Z)
                    //move to identifier's state
                    if (currentChar.matches("[a-zA-Z]")) {
                        currentState = States.INID;
                    }
                    
                    //Incase of whitespace, continue
                    //no need to process
                    if (currentChar.matches(" ")) {
                        currentState = States.START;
                        continue;
                    }
                    
                    //Incase of number, move to numbers state
                    if (isNum(currentChar)) {
                        currentState = States.INNUM;
                    }
                    //Incase of curly bracket. Comment State
                    if (currentChar.matches("[{]")) {
                        currentState = States.INCOMMENT;
                    }
                    
                    //Incase of :, Assignment State transition
                    if (isCol(currentChar)) {
                        currentState = States.INASSIGN;
                    }
                    
                    //Incase of Symbol, didn't have to make a special state for it
                    //since it is always one character
                    if (isSymbol(currentChar)) {
                        currentState=States.DONE;
                    }

                }

                
                else if(currentState==States.INCOMMENT) {
                    
                    //moving to done state in order to setup the token
                    if (currentChar.matches("[}]")) {
                        currentState = States.DONE;
                    }
                    //stay inside INCOMMENT STATE, since
                    // we are still inside the curly bracket
                    else {
                        currentState = States.INCOMMENT;
                    }
                }

                
                else if(currentState==States.INID) {
                
                    //in case currentChar is a-z, A-Z, or _
                    if (currentChar.matches("[a-zA-Z_]")) {
                        currentState = States.INID;

                    }
                    //move to DONE state, in order to tokenize
                    //since everything after white space should
                    //not be related to it
                    else if (currentChar.matches(" ")) {
                        currentState = States.DONE;

                    }
                    
                    //another character like ;
                    //move to OTHER STATE
                    else{
                        currentState=States.OTHER;
                    }
                }


                else if(currentState== States.INNUM) {
                    
                    //still in numbers state
                    if (currentChar.matches("[0-9]+")) {

                        currentState = States.INNUM;
                    }
                    //move to done to tokenize
                    else if (currentChar.matches(" ")) {

                        currentState = States.DONE;

                    }
                    //if there is any other character
                    //move to state other
                    else{
                        currentState = States.OTHER;
                    }
                }

                
                else if(currentState==States.INASSIGN) {
                
                    //waiting for = so that the final token will be
                    //:= to be tokenized
                    if (currentChar.matches("=")) {
                        currentState = States.DONE;
                    }
                    
                    //another character, move to other state to identify
                    //its state separately.
                    else {
                        currentState=States.OTHER;
                    }
                }


                //add each currentChar to variable token
                //so that we can tokenize it in DONE State
            if(currentState!=States.OTHER)
            {
                token+=currentChar;

            }

            //moving to DONE state, and setting nextState to true
            //in order to tokenize the current present token
            //and identifying the state the current character 
            //since it should not be associated with the current token
            if(currentState==States.OTHER)
            {
                currentState=States.DONE;
                nextState=true;
            }



            if(currentState==States.DONE)
            {
                //setup the current token then add it to our tokenlist
                classify(token, tokenList);

                //in case of foreign character exists
                //(coming from OTHER STATE)
                
                if(nextState)
                {
                        //adding the foreign character to token
                        //remember that we made our token ="" in
                        //classify
                        token = currentChar;
                        
                        
                        //if current character(foreign character) is ":"
                        //current state will be INASSIGN
                        //so that starting from next iteration 
                        //we will enter its if condition
                        if(isCol(currentChar))
                        {
                            currentState=States.INASSIGN;
                        }
                        
                        //in case the current character
                        // is {
                        if(currentChar.matches("[{]"))
                        {
                            currentState=States.INCOMMENT;
                        }
                        //in case it is a number,
                        //this only happens if our token was
                        //of type identifier
                        if(isNum(currentChar))
                        {
                            currentState=States.INNUM;
                        }
                        
                        //incase it is alphabetic
                        //coming from numbers state
                        if(isStr(currentChar))
                        {
                            currentState=States.INID;
                        }
                        
                        //since it is always only one character
                        //we can classify it immediately and add it
                        //to the token list, and start over from START
                        //state
                        if (isSymbol(currentChar))
                        {
                                    classify(currentChar, tokenList);
                                    token="";
                                    currentState=States.START;
                                    
                        }

                        //resetting nextState to false
                        nextState=false;
                    }
                    //setting the token to blank, so that
                    //we can process next characters
                    else {
                        token = "";

                    }
                    
                
                    currentState = States.START;
                    }
        }

    }

    static void initializeMap()
    {
        symbolsMap.put("+","PLUS");
        symbolsMap.put("-","MINUS");
        symbolsMap.put("*","MULTIPLY");
        symbolsMap.put("/","DIVIDE");
        symbolsMap.put("=","EQUAL");
        symbolsMap.put("<","LESS_THAN");
        symbolsMap.put(">","BIGGER_THAN");
        symbolsMap.put("(","OPEN_BRACKET");
        symbolsMap.put(")","CLOSE_BRACKET");
        symbolsMap.put(";","SEMI_COLON");
        symbolsMap.put("–", "MINUS");
        symbolsMap.put("–", "MINUS");
        reservedMap.put("if","IF");
        reservedMap.put("then","THEN");
        reservedMap.put("else","ELSE");
        reservedMap.put("end","END");
        reservedMap.put("repeat","REPEAT");
        reservedMap.put("until","UNTIL");
        reservedMap.put("read","READ");
        reservedMap.put("write","WRITE");

    }
    
    //inputs: references of (String token, Array of tokenList)
    //output: setting up token and adding it to tokenList based
    //on its tokenType
    static void classify(String token, ArrayList<Token> tokenList)
    {
        //removing last white space if exists
        if(token.endsWith(" "))
        {
            token = token.substring(0,(token.length())-1);
        }

        //in case the token starts with alphabets 
        //and is considered as an identifier or reserved word
        if(isStr(token))
        {
            //in case the token is considered as 
            //reserved word (is inside reservedWords list)
            if(Arrays.asList(reservedWords).contains(token))
            {
                //reservedMap.get(token) returns the reserved word token
                //but in upper case as saved in reservedMap
                //I could have used .toUpper() method I guess...
                tokenList.add(new Token(token,reservedMap.get(token) + "  -- Reserved_Word"));

            }
            else
            {
                //add the token as an identifier normally
                tokenList.add(new Token(token,"IDENTIFIER"));

            }
        }
        
        //in case the token is an assignment
        else if(token.matches(":="))
        {
            tokenList.add(new Token(token,"ASSIGNMENT"));

        }
        //in case the token starts with { and ends with }
        else if(isComment(token)){

            tokenList.add(new Token(token,"COMMENT"));

        }
        //in case the token is numeric
        else if(isNum(token))
        {
            tokenList.add(new Token(token, "NUMBER"));
        }
        //in case the token is symbol
        else if(isSymbol(token)) {
            tokenList.add(new Token(token, symbolsMap.get(token)));

        }
        //
        //else{
          //  tokenList.add(new Token(token,"OTHER"));
        //}

    }
    
    
    /////////////////////////////////////// HELPING METHODS & REGEX ///////////////////////////////////////
    static boolean isStr(String str)
    {
        //^ starts with
        //$ ends with
        //| or
        //this regex stands for ( start with a letter ranging from a-z
        //or A-Z and then continues with one or more occurrences
        return str.matches("^[a-zA-Z][a-zA-Z]+$|[a-zA-Z]+");
    }
    static boolean isCol(String str)
    {
        return str.matches(":");
    }
    static boolean isComment(String str)
    {
        //starts with { and have zero or more occurences of
        //any character and ends with }
        return str.matches("^\\{.*}$");
    }
    //if it is considered as a symbol from 
    //our predefined list
    static boolean isSymbol(String str)
    {
        return Arrays.asList(specialSymbols).contains(str);
    }
    
    //\d+ stands for any numeric
    static boolean isNum(String str)
    {
        return str.matches("\\d+");
    }
    //used in debugging
    public static void outputScan()
    {
        for(int i=0; i<tokenList.size(); i++)
        {
            System.out.println("TYPE:   "+tokenList.get(i).tokenType+"    Value:    "+tokenList.get(i).tokenValue);
        }
    }
}

//enum of STATES
enum States{
    START,
    INNUM,
    INID,
    INASSIGN,
    INCOMMENT,
    OTHER,
    DONE
}
