package slaynash.sgengine.audio;

import paulscode.sound.SoundSystemLogger;
import slaynash.sgengine.LogSystem;

public class AudioLogger extends SoundSystemLogger {
	@Override
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
        LogSystem.out_println("[SoundSystemLogger] "+ messageText );
    }
	
	@Override
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
        LogSystem.out_println("[SoundSystemLogger] "+ messageText );
    }
	
	@Override
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
        LogSystem.out_println("[SoundSystemLogger] "+ headerLine );
        LogSystem.out_println("[SoundSystemLogger] "+ messageText );
    }
}
