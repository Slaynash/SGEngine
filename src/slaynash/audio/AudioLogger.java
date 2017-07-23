package slaynash.audio;

import paulscode.sound.SoundSystemLogger;

public class AudioLogger extends SoundSystemLogger {
	public void message( String message, int indent )
    {
        String messageText;
        // Determine how many spaces to indent:
        String spacer = "";
        for( int x = 0; x < indent; x++ )
        {
            spacer += "    ";
        }
        // indent the message:
        messageText = spacer + message;
        
        // Print the message:
        System.out.println("[SoundSystemLogger] "+ messageText );
    }
	
	public void importantMessage( String message, int indent )
    {
        String messageText;
        // Determine how many spaces to indent:
        String spacer = "";
        for( int x = 0; x < indent; x++ )
        {
            spacer += "    ";
        }
        // indent the message:
        messageText = spacer + message;
        
        // Print the message:
        System.out.println("[SoundSystemLogger] "+ messageText );
    }
	
	public void errorMessage( String classname, String message, int indent )
    {
        String headerLine, messageText;
        // Determine how many spaces to indent:
        String spacer = "";
        for( int x = 0; x < indent; x++ )
        {
            spacer += "    ";
        }
        // indent the header:
        headerLine = spacer + "Error in class '" + classname + "'";
        // indent the message one more than the header:
        messageText = "    " + spacer + message;
        
        // Print the error message:
        System.out.println("[SoundSystemLogger] "+ headerLine );
        System.out.println("[SoundSystemLogger] "+ messageText );
    }
}
