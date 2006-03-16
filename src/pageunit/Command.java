package pageunit;

/**
 * This ENUM lists all the commands that are valid in the language.
 */
public enum Command {

		SOURCE,	// '<', File Inclusion
		SET,	// '=', Set Variable
		A,	// As user [pw]
		B,	// set Base URL
		C,	// Configuration
		D,	// debug on/off
		E,	// echo
		F,	// Find Form
		G,	// Go to link
		H,	// hard-code hostname
		J,	// get J2EE protected page
		L,	// page contains Link
		M,	// page contains text
		N,	// start new session
		O,	// hard-code pOrt number
		P,	// get Unprotected page
		R,	// set parameter to value
		S,	// SUBMIT
		T,	// page contains tag with text (in bodytext or attribute value)
		V,	// Verify (Link Checker) - may take a long time!
		X,	// XTENTION or PLUG-IN
		Y,	// REMOVE XTENTION or PLUG-IN

}
