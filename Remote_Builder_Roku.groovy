/**
*  Remote Builder TV Remote
*  Version: See ChangeLog
*  Download: See importUrl in definition
*  Description: Used in conjunction with child apps to generate tabular reports on device data and publishes them to a dashboard.
*
*  Copyright 2024 Gary J. Milne  
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.

*  License:
*  You are free to use this software in an un-modified form. Software cannot be modified or redistributed.
*  You may use the code for educational purposes or for use within other applications as long as they are unrelated to the 
*  production of tabular data in HTML form, unless you have the prior consent of the author.
*  You are granted a license to use Remote Builder in its standard configuration without limits.
*  Use of Remote Builder in it's Advanced requires a license key that must be issued to you by the original developer. TileBuilderApp@gmail.com
*
*  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
*  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 

*  Authors Notes:
*  For more information on Remote Builder check out these resources.
*  Original posting on Hubitat Community forum: TBD
*  Remote Builder Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf
*
*  Remote Builder Roku Remote - ChangeLog
*
*  Gary Milne - September 5th, 2024 @ 8:58 AM
*
*  Version 1.1.3 - Initial Public Release based on Remote Builder TV skeleton version 1.1.3
*
**/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field

static def buttonGroup() { return  }

@Field static final codeDescription = "<b>Remote Builder - Roku 1.1.3 (9/4/24)</b>"
@Field static final codeVersion = 113
@Field static final moduleName = "Roku Remote"

def deviceProfileList() { return [0:'Roku Connect (>= 2.8.2) by Armand Welsh'] }

definition(
	    name: "Remote Builder - Roku",
        description: "Generates a TV remote control that can operate be executed from a web browser or embedded into a Hubitat Dashboard.",
        importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Roku.groovy",
        namespace: "garyjmilne", author: "Gary J. Milne", category: "Utilities", iconUrl: "", iconX2Url: "", iconX3Url: "", singleThreaded: false,
        parent: "garyjmilne:Remote Builder", 
        installOnOpen: false, oauth: true
)

//Tells the App how to direct inbound and outbound requests.
mappings {
    path("/tb") {
        action: [POST: "response",
                 GET: "showApplet"]
    }
}

preferences {
   page name: "mainPage"
}


def mainPage(){
    //Basic initialization for the initial release
    if (state.initialized == null) initialize()
    
    dynamicPage(name: "mainPage", title: "<div style='text-align:center;color: #c61010; font-size:30px;text-shadow: 0 0 5px #FFF, 0 0 10px #FFF, 0 0 15px #FFF, 0 0 20px #49ff18, 0 0 30px #49FF18, 0 0 40px #49FF18, 0 0 55px #49FF18, 0 0 75px #ffffff;;'> Remote Builder - " + moduleName + " 📱 </div>", uninstall: true, install: true, singleThreaded:true) {
			section(hideable: true, hidden: state.hidden.Device, title: buttonLink('btnHideDevice', getSectionTitle("Device"), 20)) {
				paragraph "<b>Select the target Roku and the Device Driver Profile.</b> Only devices with the capability 'mediaInputSource' will be listed. "
				input ("myRoku", "capability.'mediaInputSource'", title: "<b>Default Roku Device</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px") /* top right bottom left */
				if (myRoku != null ) { 
					input(name: "selectedProfile", type: "enum", title: bold("Select the Device Driver Profile"), options: deviceProfileList(), required: false, defaultValue: 0, submitOnChange: true, width: 4, style:"margin-right: 20px")
				
					paragraph "Click <b>Apply Profile</b> after making your selections.  The profile selected determines the mapping of key presses to the execution of commands. These can be modified in the <b>Customize Remote</b> section.<br><b>Applying a profile wipes out the current configuration of the remote including any custom buttons!</b>"
					input(name: "applyProfile", type: "button", title: "Apply Profile", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 12)
				}
			}
        
            section(hideable: true, hidden: state.hidden.Endpoints, title: buttonLink('btnHideEndpoints', getSectionTitle("Endpoints"), 20)) {
                input(name: "localEndpointState", type: "enum", title: bold("Local Endpoint State"), options: ["Disabled", "Enabled"], required: false, defaultValue: "Enabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
                input(name: "cloudEndpointState", type: "enum", title: bold("Cloud Endpoint State"), options: ["Disabled", "Enabled"], required: false, defaultValue: "Disabled", submitOnChange: true, width: 2, style:"margin-right: 20px")
				paragraph line (1)
				
				//Display the Endpoints with links or ask for compilation
				paragraph "<a href='${state.localEndpoint}' target=_blank><b>Local Endpoint</b></a>: ${state.localEndpoint} "
                paragraph "<a href='${state.cloudEndpoint}' target=_blank><b>Cloud Endpoint</b></a>: ${state.cloudEndpoint} "
				
				myText = "<b>Important: If these endpoints are not generated you may have to enable OAuth for this application to work.</b><br>"
            	myText += "Both endpoints can be active at the same time and can be enabled or disabled through this interface.<br>"
				myText += "Endpoints are paused if this instance of the <b>Remote Builder</b> application is paused. Endpoints are deleted if this instance of <b>Remote Builder</b> is removed.<br>"
				paragraph myText
            	paragraph line (1)
            }
        
            section(hideable: true, hidden: state.hidden.Display, title: buttonLink('btnHideDisplay', getSectionTitle("Display"), 20)) {
                input(name: "displayEndpoint", type: "enum", title: bold("Endpoint to Display"), options: ["Local", "Cloud"], required: false, defaultValue: "Local", submitOnChange: true, width: 2, style:"margin-right: 20px")
                
				if ( state.compiledLocal == null || state.compiledCloud == null ) paragraph ("<span style=color:red><b>Press Compile Changes to Generate Remote</b></span>" )
				else {
					if ( displayEndpoint == "Local" ) paragraph '<iframe src="' + state.localEndpoint + '" width="180" height="420" style="border:solid" scrolling="no"></iframe>'
					if (displayEndpoint == "Cloud" ) paragraph '<iframe src="' + state.cloudEndpoint + '" width="180" height="420" style="border:solid" scrolling="no"></iframe>'
				}
				
                input(name: "Compile", type: "button", title: "Compile Changes", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 12)
				text = "<b>Important:</b> This is a live remote. Pressing any of the buttons will execute the actions assigned to the buttons in the <b>Customize Remote</b> section below.<br>"
				text += "When you make changes to properties of the remote they do not take effect until they have been Compiled.<br>"
				paragraph text
				
				text = "The default purpose for the buttons are as follows from left to right:<br>"
				text += "<b>Line 1:</b> Button:Power    LED1:Flicker orange = Data sent    Icon: Either house or a cloud indicating active endpoint    LED2: Solid Green = Data received    Button: Source.<br>"
				text += "<b>Line 2:</b> Custom button A: Off    Up Arrow: Navigate Up    Custom button B: channelList<br>"
				text += "<b>Line 3:</b> Left Arrow: Navigate left    OK button: Enter    Right Arrow: Navigate right<br>"
				text += "<b>Line 4:</b> Down Arrow: Navigate down<br>"
				text += "<b>Line 5:</b> Up Arrow: Volume Up    Up Arrow: Channel Up<br>"
				text += "<b>Line 6:</b> Speaker: Mute toggle    Three lines: Channel Guide<br>"
				text += "<b>Line 7:</b> Down Arrow: Volume Down    Down Arrow: Channel Down<br>"
				text += "<b>Line 8:</b> Button: Exit\\Back    Button: Home    Button: Menu<br>"
				text += "<b>Line 9:</b> Button: Fast Back    Button: Play\\Pause    Button: Fast Forward<br>"
				text += "<b>Line 10:</b> Custom Buttons default to Netflix, Amazon Prime, Disney+ and HBO Max<br>"
				text += "<b>Line 11:</b> Custom Buttons default to inactive<br>"
				paragraph summary ("Explanation of Remote Layout", text)
            }
        
			def data = getProfile()
			def fixedButtonCount = data.fixedButtonCount
			def customButtonCount = data.customButtonCount
		
			section(hideable: true, hidden: state.hidden.Customize, title: buttonLink('btnHideCustomize', getSectionTitle("Customize"), 20)) {
				def startIndex, endIndex
				input(name: "selectedButtonGroup", type: "enum", title: bold("Select Button Group"), options: ['FIXED', 'CUSTOM'], required: true, defaultValue: "FIXED", submitOnChange: true, width: 2)
				input(name: "showParameters", type: "enum", title: bold("Show Parameters"), options: ['TRUE', 'FALSE'], required: true, defaultValue: "FALSE", submitOnChange: true, width: 2)								   
				if(selectedButtonGroup == "FIXED") input(name: "commandsPerLine", type: "enum", title: bold("Commands Per Line"), options: ['1', '2','3','4','5'], required: true, defaultValue: "2", submitOnChange: true, width: 2)								   
				paragraph( line(1) )
				
				if(selectedButtonGroup == "FIXED") { 
					//Main loop that places the control on the screen
					(1..fixedButtonCount).each { i ->
						index = i
						input ("myCommand$i", "enum", title: "&nbsp<b>Command</b> (" + data.fixedButtons["button${index}"].text.toString() + ")", options: getCommandList(settings["myRoku"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
						if (showParameters == "TRUE") input ("myParameter$i", "text", title: "&nbsp<b>Parameter</b>", multiple: false, submitOnChange: true, width: 1, style: "margin: 2px 60px 2px 10px; padding:3px")
						if ( (i ) % commandsPerLine.toInteger() == 0 ) paragraph( line(1) )
					}
				}
				
				if(selectedButtonGroup == "CUSTOM") { 
					endIndex = 50 + customButtonCount
					//Main loop that places the control on the screen	
					(51..endIndex).each { i ->
						input ("myDevice$i", "capability.*", title: "<b>Custom Button " + ( i - 50 ) + " Device</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px") /* top right bottom left */
						input ("myCommand$i", "enum", title: "&nbsp<b>Command</b>", options: getCommandList(settings["myDevice$i"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
						if (showParameters == "TRUE") input ("myParameter$i", "text", title: "&nbsp<b>Parameter</b>", multiple: false, submitOnChange: true, width: 1, style: "margin: 2px 60px 2px 10px; padding:3px")
				    	input ("myButtonColor$i", "color", title: "&nbsp<b>Button Color</b>", defaultValue: "#FFFFFF", required: false, width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
						input ("myText$i", "text", title: "&nbsp<b>Character</b>", multiple: false, submitOnChange: true, width: 1, required: true, style: "margin: 2px 10px 2px 10px; padding:3px;border: 1px solid gray")
                    	input ("myTextColor$i", "color", title: bold("&nbsp<b>Text Color</b>"), required: false, defaultValue: "#FFFFFF", width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
						paragraph( line(1) )
                	} 
				}
				
				paragraph line(2)
				input(name: "unassignedButtonBehaviour", type: "enum", title: bold("Unassigned Button Behaviour"), options: ["Normal", "Disabled", "Hidden"], required: false, defaultValue: "Normal", submitOnChange: true, width: 2, newLine: true, style:"margin-right: 20px")
				input(name: "enableHapticResponse", type: "enum", title: bold("Enable Haptic Response"), options: ["True", "False"], required: false, defaultValue: "False", submitOnChange: true, width: 2)
				
				text = "The contents of the <b>Command</b> drop down list is retrieved from the device and contains all available commands, plus some Remote Builder added commands described below.<br><br>"
				text += "<b>Synthetic Commands</b><br>"
				text += "Commands beginning with an <b>*</b>, such as *toggle are synthetic commands that don't exist within the device but are are added if the equivalent command does not already exist.<br><br>"
				text += "<b>Arithmetic Parameters</b><br>"
				text += "Commands ending with an <b>*</b>, such as setLevel*, volumeLevel* etc have values in the range 0 - 100 and support simple arithmetic parameters. These arithmetic parameters make a single button click more powerful such as lowering a dimmer by 25% or raising a blind by 10% with each click instead of using fixed values.<br>"
				text += "Supported arithmetic parameters are as follows: <br>"
				text += "<b>Addition:</b> Entering a parameter in the form <mark><b>+<i>number</i></b></mark> will add the parameter value to the existing value up to a maximum value of 100. Examples: <mark><b>+2</b></mark> , <mark><b>+5</b></mark> , <mark><b>+20</b></mark><br>"
				text += "<b>Subtraction:</b> Entering a parameter in the form <mark><b>-<i>number</i></b></mark> will subtract the parameter value from the existing value down to a minimum of 0. Examples: <mark><b>-2</b></mark> , <mark><b>-10</b></mark> , <mark><b>-25</b></mark><br>"
				text += "<b>Multiplication:</b> Entering a parameter in the form <mark><b>*<i>number</i></b></mark> will multiply the existing value by the parameter value up to a maximum value of 100. Examples: <mark><b>*2</b></mark> , <mark><b>*1.1</b></mark> , <mark><b>*0.5</b></mark><br>"
				text += "<b>Division:</b> Entering a parameter in the form <mark><b>/<i>number</i></b></mark> will divide the existing value by the parameter value down to a minimum of 0. Examples: <mark><b>/3</b></mark> , <mark><b>/2</b></mark> , <mark><b>/1.1</b></mark><br><br>"
				text += "<b>Note:</b> All of these commands operate on a scale of 0 - 100. If a dimmer were at 50 then the -10 command will lower the value to 40.  Using *0.9 would lower the dimmer from 50 to 45 so these commands are not equal (except at a value of 100).<br>"
				text += "<b>Note:</b> A dimmer starting at 100 with a command of /2 would step down as follows: 100, 50, 25, 12, 6, 3, 1. Of course dividing by 2 is the same as multiplying by 0.5, it's just a matter of personal preference.<br><br>"
				text += "<b>Passing Multiple Parameters</b><br>"
				text += "Some commands take multiple parameters but you can provide multiple parameters even though there is only a single parameter field. This is accomplished by using the # character as a seperator.<br>"
				text += "<b>Example 1:</b> The <b>setColorTemperature</b> with parameter <mark><b>2900#100#10</b></mark> will set the color temperature to 2900, the level to 100 and the transition time for this change will be 10 seconds.<br>"
				text += "<b>Example 2:</b> The <b>setLevel</b> with parameter <mark><b>10#5</b></mark> will change the light level from its current setting to a value of 10 over the next 5 seconds.<br><br>"
				text += "<b>Passing a Map as a Parameter</b><br>"
				text += "The <b>setColor</b> command is unusual in that it takes it's arguments the form of a map. The following examples show the proper way to format a map argument for use with setColor.<br>"
				text += "<b>setColor:</b> After selecting the <b>setColor</b> command from the dropdown menu enter the map arguments like this: <mark><b>['hue':20,'saturation':38,'level':24]</b></mark><br>"
				text += "<b>setColor:</b> Because the level parameter is optional in <b>setColor</b> you may also use the form: <mark><b>['hue':65,'saturation':77]</b></mark><br>"
				text += "For information on the required parameters for device commands you can find the reference here: <a href='https://docs2.hubitat.com/en/developer/driver/capability-list' target=_blank> <i><b>Driver Capability Reference</b></i></a>"
				paragraph summary ("Commands and Parameters Help", text)
				
            }
        
        //Start of Publish Section
		section(hideable: true, hidden: state.hidden.Publish, title: buttonLink('btnHidePublish', getSectionTitle("Publish"), 20)) {
            input(name: "myRemote", title: "<b>Attribute to store the Remote? (Optional)</b>", type: "enum", options: parent.allTileList(), required: false, submitOnChange: true, width: 3, defaultValue: 0, newLine: false)
            input(name: "myRemoteName", type: "text", title: "<b>Name this Remote</b>", submitOnChange: true, width: 3, defaultValue: "New Roku Remote", newLine: false, required: false)
            input(name: "tilesAlreadyInUse", type: "enum", title: bold("For Reference Only: Remotes in Use"), options: parent.getTileList(), required: false, defaultValue: "Remotes List", submitOnChange: true, width: 3)
                                    
            if (myRemoteName) app.updateLabel(myRemoteName)
            myText =  "Publishing a remote is optional and only required if it will be used within a dashboard. Remotes can be accessed directly via the URL's in the Endpoints section and bypass the Dashboard entirely. The <b>Remote Name</b> given here will also be used as the name for this instance of Remote Builder. "
			myText += "Appending the name with your chosen remote number can make parent display more readable.<br>"
            myText += "Only links to the Local and Cloud Endpoints are stored in the Remote Builder Storage Device when publishing is enabled.<br>"
            paragraph myText																																																																													 
            paragraph line(1)
            
            if ( state.compiledLocal != null  && state.compiledCloud && settings.myRemote != null && myRemoteName != null) {
                input(name: "publishRemote", type: "button", title: "Publish Remote", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 12)
            } else input(name: "cannotPublish", type: "button", title: "Publish Remote", backgroundColor: "#D3D3D3", textColor: "black", submitOnChange: true, width: 12)
        }
        //End of Publish Section
		
		//Start of More Section
        section {
            input(name: "isMore", type: "bool", title: "More Options", required: false, multiple: false, defaultValue: false, submitOnChange: true, width: 2)
            if (isMore == true) {
                //Horizontal Line
				paragraph "In this section you can enable logging of any connection and action requests received. You can also rebuild the endpoints if you choose to refresh the OAuth client secret."				
                input(name: "isLogDebug", type: "bool", title: "<b>Enable Debug logging?</b>", defaultValue: false, submitOnChange: true, width: 3, newLine: true)
                input(name: "isLogErrors", type: "bool", title: "<b>Log errors encountered?</b>", defaultValue: true, submitOnChange: true, width: 3)
				input(name: "isLogConnections", type: "bool", title: "<b>Record all connection requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogActions", type: "bool", title: "<b>Record all action requests?</b>", defaultValue: true, submitOnChange: true, width: 3)
				input(name: "rebuildEndpoints", type: "button", title: "<b>Rebuild endpoint(s)</b>", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, newLine:true)
            }
            //Now add a footer.
            myDocURL = "<a href='https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf' target=_blank> <i><b>Remote Builder Help</b></i></a>"
            myText = '<div style="display: flex; justify-content: space-between;">'
            myText += '<div style="text-align:left;font-weight:small;font-size:12px"> <b>Documentation:</b> ' + myDocURL + '</div>'
            myText += '<div style="text-align:center;font-weight:small;font-size:12px">Version: ' + codeDescription + '</div>'
            myText += '<div style="text-align:right;font-weight:small;font-size:12px">Copyright 2022 - 2024</div>'
            myText += '</div>'
            paragraph myText
        }
        //End of More Section

    }
}

//Returns all the data for a given device profile in a Map
def getProfile(){
	
	def fixedButtons
	def fixedButtonCount = 0
	def customButtons
	def customButtonCount = 0
	
	if (isLogDebug) log.debug ("Selected Profile is: " + selectedProfile.toInteger() )
	
	switch(selectedProfile.toInteger()){
        case [0]: /* Roku Connect >= 2.8.2  */	
			fixedButtons = [
				button1: [text: "Power", command: "keyPress", parameter: "Power"],
 				button2: [text: "⬅", command: "keyPress", parameter: "Back"],
				button3: [text: "🏠︎", command: "home", parameter: "?"],
 				button4: [text: "▲", command: "keyPress", parameter: "Up"],
				button5: [text: "◀", command: "keyPress", parameter: "Left"],
				button6: [text: "▶", command: "keyPress", parameter: "Right"],
 				button7: [text: "▼", command: "keyPress", parameter: "Down"],
 				button8: [text: "OK", command: "keyPress", parameter: "Enter"],
				
				button9: [text: "↺", command: "keyPress", parameter: "InstantReplay"],
				button10: [text: "☽", command: "guide", parameter: "?"],
				button11: [text: "*", command: "keyPress", parameter: "Info"],
				
				button12: [text: "◀◀", command: "keyPress", parameter: "Rev"],
 				button13: [text: "▶ \\ ❚❚", command: "play", parameter: "?"],
 				button14: [text: "▶▶", command: "keyPress", parameter: "Fwd"],
				
				button15: [text: "🔉", command: "volumeDown", parameter: "?"],
 				button16: [text: "🔇", command: "mute", parameter: "?"],
				button17: [text: "🔊", command: "volumeUp", parameter: "?"],
 				
				button18: [text: "NETFLIX", command: "setInputSource", parameter: "Netflix"],
 				button19: [text: "Disney+", command: "setInputSource", parameter: "Disney Plus"],
 				button20: [text: "🍎&#xFE0E tv+", command: "setInputSource", parameter: "Apple TV"],
 				button21: [text: "The Roku Channel", command: "setInputSource", parameter: "The Roku Channel"]
			]
			
			customButtons = [
 				button51: [command: "", parameter: "?", color: "#FF0000", text: "1", textColor: "#FFFFFF"],
    			button52: [command: "", parameter: "?", color: "#FFA500", text: "2", textColor: "#FFFFFF"],
    			button53: [command: "", parameter: "?", color: "#0000FF", text: "3", textColor: "#FFFFFF"],
    			button54: [command: "", parameter: "?", color: "#008000", text: "4", textColor: "#FFFFFF"]
			]
			
			break
	
        default:
            return [ "No Device Profile Found"]
			}
	
	customButtonCount = customButtons.size()
	fixedButtonCount = fixedButtons.size()
	
	buttonData = [ fixedButtons: fixedButtons, fixedButtonCount: fixedButtonCount, customButtons: customButtons, customButtonCount: customButtonCount ]
	if (isLogDebug) log.debug ("Data is: $buttonData")		   
	return buttonData
}

//Sets the default actions for each of the buttons.
def applyProfile(){
	
	
	
	def data = getProfile()
	def fixedButtonCount = data.fixedButtonCount
	
	// Loop from 1 to fixedButtonCount and assign each buttonCommand to the appropriate myCommand setting.
	(1..fixedButtonCount).each { index ->
		def command = data.fixedButtons["button${index}"].command
		def parameter = data.fixedButtons["button${index}"].parameter
		app.updateSetting("myCommand${index}", [value:"$command", type:"enum"])
		app.updateSetting("myParameter${index}", [value:"$parameter", type:"enum"])
	}	
	
	//Custom buttons start at 51 and end at 54. These are the buttons that can be customized.
	(51..54).each { index ->
		
		app.updateSetting("myDevice${index}", [type: "capability", value: settings.myRoku ] )
		
		def myCommand = data.customButtons["button${index}"].command
		if (isLogDebug) log.info("Command is: $myCommand ")
		app.updateSetting("myCommand${index}", [value:"$myCommand", type:"enum"])
		
		def myParameter = data.customButtons["button${index}"].parameter
		if ( myParameter == null || myParameter == "" ) myParameter = "?"
		app.updateSetting("myParameter${index}", [value:"$myParameter", type:"text"])
		
		def myCustomButtonColor = data.customButtons["button${index}"].color
		if (isLogDebug) log.info ("Button Color: $myCustomButtonColor")
		app.updateSetting("myButtonColor${index}", [value: "$myCustomButtonColor", type: "color"])
		
		def myCustomText = data.customButtons["button${index}"].text
		if (isLogDebug) log.info ("Text: $myCustomText")
		app.updateSetting("myText${index}", [value: "$myCustomText", type: "text"])
		
		def myCustomTextColor = data.customButtons["button${index}"].textColor
		if (isLogDebug) log.info ("Text Color: $myCustomTextColor")
		app.updateSetting("myTextColor${index}", [value: "$myCustomTextColor", type: "color"])

	}		
	
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  Compile Functions
//**************
//*******************************************************************************************************************************************************************************************

//Compress the text output and generate the version that will be used by the browser.
def compile(){
    // Create a list to hold the JSON objects
    def jsonGroup = []
	def data
	def isActive
	def isHidden
	
    // Loop from 1 to 18 and create data for each group
    (51..54).each { i ->
        //def index = (i)
		isActive = true
		isHidden = false
		def myDevice = settings."myDevice$i"
		def myCommand = settings."myCommand$i"
		
	if ( myDevice == null || myCommand == null ) isActive = false
		
	if (isLogDebug) log.debug ("Index: $i  and isActive: $isActive and myCommand is: $myCommand")
		
		switch(unassignedButtonBehaviour){
        	case ["Normal"]:  //Show all buttons
				data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "bHidden": "false" ]
    	        break
        	case ["Disabled"]:  //Buttons are shown but are disabled
				if (isActive) data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "bHidden": "false" ]
				else data = [ "index": "$i", "label": "?", "bColor": "#555", "tColor": "#555", "bHidden": "false"]
        	    break
			case ["Hidden"]:
				if (isActive) data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "bHidden": "false" ]
				else data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "bHidden": "true" ]
    	        break
        	default:
            	break
		}
	    jsonGroup << data
    }
	
	// Convert the list of maps to a JSON string and save them to state
    state.buttonData = JsonOutput.toJson(jsonGroup)
	if (isLogDebug) log.debug ("ButtonData is: $state.buttonData")
	
    def String content = myHTML()
    content = content.replace("#buttonData#", state.buttonData )
	content = content.replace("#hapticResponse#", enableHapticResponse.toLowerCase() )
	
	// Strip all the comments out of the file to save space.
    content = condense(content)
        
    // Create separate copies of content for local and cloud versions
    def localContent = content
	localContent = localContent.replace('#connectionIcon#', '⌂' )
    localContent = localContent.replace("#URL#", state.localEndpoint )
    
	def cloudContent = content
	cloudContent = cloudContent.replace('#connectionIcon#', '☁︎' )
    cloudContent = cloudContent.replace("#URL#", state.cloudEndpoint )
		
    // Saves a copy of this finalized HTML\CSS\SVG\SCRIPT so that it does not have to be re-calculated.
    state.compiledLocal = localContent
    state.compiledCloud = cloudContent
    state.compiledSize = state.compiledLocal.size()
}

//Remove any wasted space from the compiled version to shorten load times.
def condense(String input) {
	def initialSize = input.size()
    if (isLogDebug) log.debug ("Original Size: " + initialSize )
	
	//Reduce any groups of 2 or more spaces to a single space
	input = input.replaceAll(/ {2,}/, " ")
	if (isLogDebug) log.debug ("After concurrent spaces removed: " + input.size() + " bytes." )
	
	//Remove any Tabs from the file
	input = input.replaceAll(/\t/, "")
	if (isLogDebug) log.debug ("After concurrent tabs removed: " + input.size() + " bytes." )
	
	//Replace "; " with ";"
	input = input.replaceAll("; ", ";")
	
	input = input.replaceAll("> <", "><" )
	input = input.replaceAll(" = ", "=" )
	input = input.replaceAll(" <", "<" )
	
    //Replace any comments in the HTML\CSS\SVG section that will be between <!-- and -->
    input = input.replaceAll(/<!--.*?-->/, "")
    if (isLogDebug) log.debug ("After HTML\\CSS\\SVG comments removed: " + input.size() + " bytes." )
    
    //Replace any comments in the SCRIPT section that will be between \* and *\  Note: Comments beginning with \\ will not be removed.
    input = input.replaceAll(/(?s)\/\*.*?\*\//, "")
    if (isLogDebug) log.debug  ("After SCRIPT comments removed: " + input.size() + " bytes." )
    if (isLogDebug) log.debug ("Before: " + String.format("%,d", initialSize) + " - After: " + String.format("%,d", input.size()) + " bytes.")
	
    return input 
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End of Compile Functions
//**************
//*******************************************************************************************************************************************************************************************


//*******************************************************************************************************************************************************************************************
//**************
//**************  Endpoint Activity Handling
//**************
//*******************************************************************************************************************************************************************************************

//This handles incoming response from any button.
def response(){
    // Extract the body field
    def bodyJson = request.body
    
    // Parse the JSON content of the body
    def parsedBody = new JsonSlurper().parseText(bodyJson)
    
    // Access the nested 'value' field
    def mode = parsedBody.mode
    String button = parsedBody.button
    def i = (button.replace("text","")).toInteger()
	
	if (i < 30) myDevice = settings["myRoku"]
	else myDevice = settings["myDevice$i"]
    myCommand = settings["myCommand$i"]
	myParameter = settings["myParameter$i"]
	
	// Record the action request
    if (isLogActions) log.info ( "Remote Builder Data Received - Remote: $myRemote - Name: $myRemoteName - Button: $i - Device: $myDevice - Command: $myCommand - Parameters: $myParameter")
	
	if (myDevice == null || myCommand == null) return
	
	result = assembleCommand(myCommand, myParameter)
	if (isLogDebug) log.info ("Command Array is: $result")
    
	//If the values are valid we will execute the command    	
	switch(myCommand){
		case ["setLevel*"]:
			myLevel = myDevice.currentValue('level')
			def newValue = getNewValue( myLevel, result.parameters[0] )
			myDevice."${result.command}"( newValue )
			return
		case ["setVolume*"]:
			myLevel = myDevice.currentValue('volumeLevel')
			def newValue = getNewValue( myLevel, result.parameters[0] )
			myDevice."${result.command}"( newValue )
			return
		case ["setHue*"]:
			myLevel = myDevice.currentValue('hue')
			def newValue = getNewValue( myLevel, result.parameters[0] )
			myDevice."${result.command}"( newValue )
			return
		case ["setSaturation*"]:
			myLevel = myDevice.currentValue('saturation')
			def newValue = getNewValue( myLevel, result.parameters[0] )
			myDevice."${result.command}"( newValue )
			return
		case ["setPosition*"]:
			myLevel = myDevice.currentValue('position')
			def newValue = getNewValue( myLevel, result.parameters[0] )
			myDevice."${result.command}"( newValue )
			return
		case ["setTiltLevel*"]:
			myLevel = myDevice.currentValue('tilt')
			def newValue = getNewValue( myLevel, result.parameters[0] )
			myDevice."${result.command}"( newValue )
			return
		case ["setColor"]:
			def map = evaluate(myParameter)
			myDevice."${result.command}"( map )
			return
        case ["*toggle"]:
			if (myDevice.currentValue('switch') == 'on') { myDevice.off() }
			else myDevice.on()
            return
        default:
			if (myDevice != null && myCommand != null ) {
				if (myParameter == null || myParameter == "?" || myParameter == "" ) myDevice."${result.command}"()
				if (result.parameterCount == 1 ) myDevice."${result.command}"( result.parameters[0] )
				if (result.parameterCount == 2 ) myDevice."${result.command}"( result.parameters[0], result.parameters[1] )
				if (result.parameterCount == 3 ) myDevice."${result.command}"( result.parameters[0], result.parameters[1], result.parameters[2] )
			}
			return
		}
}

//Takes the existing value of the attribute and the parameter.  If the parameter has a leading +,- or * then the existing value will be adjusted by that much. If not then the value will be set to the integer value of the specified parameter.
def getNewValue(oldValue, parameter) {
    if (isLogDebug) log.debug("newValue: $oldValue , $parameter")
    def operator = parameter.find(/[\+\-\*\/]/)
    def modifierValue = operator ? convertToNumber(parameter.replaceAll("[+\\-*/]", "")) : convertToInt(parameter)

    if (modifierValue == null) {
        log.error("Input <mark><b> $parameter </b></mark> is not a valid number. Nothing done.")
        return 0
    }

    def result = operator ? 
        operator == '+' ? oldValue + modifierValue :
        operator == '-' ? oldValue - modifierValue :
        operator == '*' ? oldValue * modifierValue :
        operator == '/' ? oldValue / modifierValue : oldValue
        : modifierValue

    result = Math.max(0, Math.min(100, result.toInteger()))
    
    if (isLogActions) log.info("getNewValue: New value is: $result")
    return result
}

//Takes a string and tries to convert it to a double or integer
def convertToNumber(String input) {
    // Check if the string is an integer
	if (input.isInteger()) { return input.toInteger() } // Convert to Integer
    
    // Otherwise, assume it's a float and convert it to a Double
	else if (input.isDouble()) { return input.toDouble() } // Convert to Double
    
    // If the input is neither an integer nor a float return null
    else { return null }
}

//Takes a string and tries to convert it to an integer.  The controls only accept integers in the range 0 - 100.
def convertToInt(String input) {
    // First, check if the string is an integer
    if (input.isInteger()) { return input.toInteger() } 
    
    // Otherwise, assume it's a float and convert it to a double first
    else if (input.isDouble()) {
        double floatValue = input.toDouble()
        return floatValue.toInteger()  // Truncate the decimal part
    } 
    
    // If the input is neither an integer nor a float return null
    else { return null }
}
	

// Assemble the command string
def assembleCommand(myCommand, myParameters) {
	log.info ("assembleCommands: '$myCommand' -  '$myParameters' ")
    def result = [command: "", parameters: [], parameterCount: 0]
    
	//If we have no command just return the empty map
	if ( myCommand == null || myCommand == "" ) return result
	else {
		myCommand = myCommand.replace("*","")
		result['command'] = myCommand
	}
	
	//A ? is the default value and can be ignored.
	if (myParameters == "?" || myParameters == "null" || myParameters == null ) {
	    result['parameters'] = []
		result['parameterCount'] = 0
		return result
	}
		
    // Split the parameter string by the '#' character
    def parts = myParameters.split('#')
    
    // If there are no parameters we can just return what we already have.
    if (parts.size() == 0) return result
	
    if (parts.size() > 0) {
            // The rest are the parameters
            result['parameters'] = parts[0..-1]
            result['parameterCount'] = result['parameters'].size()
        }

    return result
}



//This delivers the applet content.
def showApplet() {
    def isLocal = false
    def isCloud = false
    def url
    
	def host = request.headers?.Host?.first()
	def protocol = request.headers?.'X-hubitat-scheme'?.first()
	
    if(request.requestSource == "local") { isLocal = true ; if (isLogConnections) log.info("Connection: $request.requestSource with protocol $protocol and host $host.") }
    if(request.requestSource == "cloud") { isCloud = true ; if (isLogConnections) log.info("Connection: $request.requestSource and host $host.") }
    
    if ( localEndpointState == "Disabled" && isLocal == true) {
        result = render contentType: "text/html;charset=UTF-8", data:disabledEndpointHTML(), status:200
        return result
    }
    
    if ( cloudEndpointState == "Disabled" && isCloud == true) {
        result = render contentType: "text/html;charset=UTF-8", data:disabledEndpointHTML(), status:200
        return result
    }
        
    //If it gets this far the interface must be enabled.
    if( isLocal ) { result = render contentType: "text/html;charset=UTF-8", data:state.compiledLocal, status:200  }
    if( isCloud ) { result = render contentType: "text/html;charset=UTF-8", data:state.compiledCloud, status:200  }
    return result
}

def disabledEndpointHTML(){
    myHTML = '''<body style="display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; font-family: Arial, sans-serif; background-color: #f0f0f0;">
    <div style="text-align: center; font-size: 24px; line-height: 1.5;color:red;">Endpoint<br>Disabled</div></body>'''
    return myHTML
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  End of Endpoint Activity Handling
//**************
//*******************************************************************************************************************************************************************************************




//*******************************************************************************************************************************************************************************************
//**************
//**************  Remote Control APPlet Code
//**************
//*******************************************************************************************************************************************************************************************

//This contains the whole HTML\CSS\SVG\SCRIPT file equivalent. Placing it in a function makes it easy to collapse.
static def myHTML() {
def HTML = 
'''
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ROKU Remote</title>
	
	<!-- Add a small default Icon that will appear on the browser tab -->
	<link rel="icon" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsTAAALEwEAmpwYAAAC20lEQVR4nO3W60tTcRzHcSH/nQ2ndjNt08ikkqmb9CCNoPKhtwcKgRloT0ywzbTZFLQ2b+F9Yup0pnl3c+6cNam5Rk7bRZ0JEl7iE+cXBYX6Ox71UX7hxcbO+f1+bzgPzkJCTud09hiptDBULFHliiRquyhMtS2WqCEEt1YkUbGicFU2t+chDld3Cz30gJguXhFiiSr3uA//ExGuyqYGiCQq9qQCxGEqGz0gTPgz52GLxyNQ4yRRAyIjKrCXiIOE80cNuHS2CnuJOkgkf9SA2PM1oJGdE44acO1iHTi66kl8C/oJB+tC1r0OxF+oPTJqwM0oHTgtr834sRuEMq4BlolP8C4t41Z8I9obLLBOO1FbOQ55jB7v+uyo00wgRVaPzuZZGN5YYTQweFH6HonRegz3f0DpYxPZk0MNUMQ0gtOut5CA+0ltYMwueNwejBod2FgP4FXlJLmm00wi4PVi3DSPqZGPCK74kJNuQHDVh8FuO5SXm8h9bToLUqIbCWpAuqwVHEP9HFn8fXOVfBZnDmIjGMDsmBNpslaseL1gZ1xY8fmwuxMk91QUjSJN2or1VT9Gehy4E9tGfu/SW3Fb2kJQAzKuGMB528CSxQV3B7C5sYKZISccZjfW/H6UPxzF7s4aeptYrPp8YCY+w2n/QgKLMkzwe75icWEJdaXTZI+eegYP4roIakBWfB84nTUM/Ite5CT0Q1diId/1pWbMz7ixHvBjqs+JPLkRC6wHQy3zyE82wjHthtvhQYfWhsCyF0uuZXRoGbRqbMi82ktQA/ITBrGfPEEG/kINeHRjGHwVXD88asCTxDEIVcwDNeCpfAqHVSKf5I0aUJZk3n6WbMa/ypJmjk4+TX8dlydb2OcpszgZFvofEk3yXLZGYcP+5gSrUlgzqQGFUlNoldLW9VLJ4FgpbB3Nqc1nqAG/I2qUTLZWyTDaVGarOpWFENzaX3vYMnkffjr/3fwEF9B6qvp1zFIAAAAASUVORK5CYII=" type="image/png">
	
    <style> 
        html, body {touch-action: manipulation; font-family: Arial, sans-serif, "Segoe UI Symbol"; overflow:hidden;}
		.button-text { font-family: Arial; font-size: 14px; fill: white; text-anchor: middle; dominant-baseline: middle; -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; }
		.button-text-materials { font-family: "Material Symbols Outlined"; fill:Red; font-size: 16px; text-anchor: middle; dominant-baseline: middle; -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; }
		.control { cursor:pointer; }
        .no-cursor { font-size:14px; font-weight:bold; pointer-events:none; text-anchor:middle; }
		.shadowed-text {color: black; text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.7); }
				
		/* Animation */
        .flicker {animation:flickerOrange 0.25s linear forwards}  
        @keyframes flickerOrange {0%, 20%, 40%, 60%, 80%, 100% {fill:#555} 10%, 30%, 50%, 70%, 90% {fill:orange}}
    </style>
</head>

<body>
    <div class="container" style="display:flex; justify-content:center; align-items:center; height:98vh; overflow:hidden;">
        <svg id="remote" viewBox="0 0 140 420" preserveAspectRatio="xMidYMid meet" style="width:100%; height:85%">
            <!-- Define the radial gradient for the shadow effect -->
            <defs>
                <linearGradient id="shadow-effect" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" stop-color="white" stop-opacity="0.3" />
                    <stop offset="100%" stop-color="black" stop-opacity="0.7" />
                </linearGradient>
                <linearGradient id="vertical-gradient" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="20%" stop-color="#555" />
                    <stop offset="50%" stop-color="#333" />
					<stop offset="80%" stop-color="#555" />
                </linearGradient>
			    <radialGradient id="radial-gradient" cx="50%" cy="50%" r="50%" fx="50%" fy="50%">
					<stop offset="20%" stop-color="#444" />
					<stop offset="50%" stop-color="#333" />
					<stop offset="80%" stop-color="#444" />
				</radialGradient>
            </defs>
            
            <!-- Remote body -->
			<rect x="10" y="10" width="120" height="400"  fill="#333" rx="20" ry="20" stroke="#222" stroke-width="5" />
			
			<!-- Local\\Cloud Indicator -->
            <text class="no-cursor" x="50%" y="6%" fill="#fff" >#connectionIcon#</text>

            <!-- Power button - This is more complex to do but it avoids loading the material font just to ge the power symbol -->
			<circle cx="50%" cy="11%" r="12" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
			<path d="M 73,40.5 A 6,6 0 1,1 67,40.5" fill="none" stroke="red" stroke-width="1" />
            <text class="button-text control numeric-button" x="50%" y="10%" style="font-size:8px; fill:Red; font-weight:900">|</text>
			<!-- We add this statement to make the clickable area about the same size as the button. It is made transparent for obvious reasons -->
			<text id="text1" class="button-text control numeric-button" x="50%" y="11%" style="font-size:20px; fill:Transparent; font-weight:900">X</text>
			
            <!-- LED -->
            <circle id="led1" cx="20%" cy="7%" r="2.5%" fill="#555"/>
            <circle id="led2" cx="80%" cy="7%" r="2.5%" fill="#555"/>
            
			<!-- Placing this hex string (&#xFE0E) after a character forces the rendering of the character without using eMoji's which is an issue mainly of Apple Devices -->

            <!-- Back Button -->
			<rect x="16%" y="15%" width="28%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text2" class="button-text control numeric-button" x="30%" y="18%" style="font-size:20px">⬅&#xFE0E</text>
			
			<!-- Home Button -->
			<rect x="56%" y="15%" width="28%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text3" class="button-text control numeric-button" x="70%" y="17.5%">🏠︎&#xFE0E</text>
			
            <!-- Navigation buttons -->
			<!-- The shaded rectangular pad -->
			<rect x="20" y="90" width="100" height="100" rx="40" ry="40" fill="url(#radial-gradient)" stroke="#333" stroke-width="1" />
			<!-- The Navigation cross -->
			<path d="M 65 95   h 10   a 10 10 0 0 1 10 10 v 20 h 20    a 10 10 0 0 1 10 10 v 10    a 10 10 0 0 1 -10 10 h -20 v 20   a 10 10 0 0 1 -10 10 h -10    a 10 10 0 0 1 -10 -10 v -20 h -20   a 10 10 0 0 1 -10 -10 v -10    a 10 10 0 0 1 10 -10 h 20 v -20   a 10 10 0 0 1 10 -10 z" fill="#3F2A87" stroke="url(#shadow-effect)" stroke-width="1"/>

			<!-- Text on the top limb -->
			<text id="text4" class="button-text control numeric-button" x="72" y="107" transform="rotate(-90, 72, 107)" style="font-size:20px" >&gt</text>
			
			<!-- Text on the left limb -->
			<text id="text5" class="button-text control numeric-button" x="40" y="142" style="font-size:20px" >&lt</text>

			<!-- Text on the right limb -->
			<text id="text6" class="button-text control numeric-button" x="100" y="142" style="font-size:20px">&gt</text>

			<!-- Text on the bottom limb -->
			<text id="text7" class="button-text control numeric-button" x="72" y="170" transform="rotate(-90, 72, 170)" style="font-size:20px">&lt</text>
            
            <!-- OK button -->
            <circle cx="70" cy="140" r="14" fill="#3F2A87" stroke="url(#shadow-effect)" stroke-width="1" />
            <text id="text8" class="button-text control numeric-button" x="70" y="141" style="font-size:12px">OK</text>
                
			<!-- Replay Button -->
			<rect x="12%" y="48%" width="22%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text9" class="button-text control numeric-button" x="33" y="213"  transform="rotate(-75, 33, 213)" style="font-size:16px; font-weight:500">↺</text>
			
			<!-- Sleep Button -->
			<rect x="40%" y="48%" width="20%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text10" class="button-text control numeric-button" x="50.2%" y="50.9%" >☽</text>
				
			<!-- Asterisk Button -->
			<rect x="66%" y="48%" width="22%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text11" class="button-text control numeric-button" x="77.5%" y="52.2%" style="font-size:24px">*</text>	
			
			<!-- Fast Back Button -->
			<rect x="12%" y="55%" width="18%" height="6%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text12" class="button-text control numeric-button" x="20.5%" y="58.1%" style="font-size:8px; letter-spacing:0px;" >◀&#xFE0E◀&#xFE0E</text>
			
			<!-- Play Button -->
			<rect x="35%" y="55%" width="30%" height="6%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text13" class="button-text control numeric-button" x="50%" y="58.1%" text-anchor="middle" dominant-baseline="middle" style="font-size:10px; letter-spacing:0px;">▶&#xFE0E❚❚</text>
				
			<!-- Fast Forward Button -->
			<rect x="70%" y="55%" width="18%" height="6%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text14" class="button-text control numeric-button" x="79.5%" y="58.1%" style="font-size:8px; letter-spacing:0px;">▶&#xFE0E▶&#xFE0E</text>	
						
			<!-- Volume Down Button -->
			<rect x="12%" y="63%" width="22%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<!-- <text id="text33" class="button-text control numeric-button" x="24%" y="66%" text-anchor="middle" dominant-baseline="middle" style="font-size:12px">🔉</text> -->
			<text id="text15" class="button-text control numeric-button" x="24%" y="65.8%" style="font-size:12px">🔉</text>
			
			<!-- Mute Button -->
			<rect x="40%" y="63%" width="20%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text16" class="button-text control numeric-button" x="50%" y="65.8%" style="font-size:12px">🔇</text>
				
			<!-- Volume Up Button -->
			<rect x="66%" y="63%" width="22%" height="5%" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" rx="10" ry="10"/>
			<text id="text17" class="button-text control numeric-button" x="78%" y="65.8%" style="font-size:12px">🔊</text>	

			<!-- Netflix Button -->
			<rect x="12%" y="71%" width="35%" height="5%" fill="#F00" stroke="url(#shadow-effect)" stroke-width="3" rx="5" ry="5"/>
			<text id="text18" class="button-text control numeric-button shadowed-text" x="29.5%" y="73.6%" style="font-size:9.5px; letter-spacing:-0.25px; font-weight:800">NETFLIX</text>
			
			<!-- Disney Button -->
			<rect x="53%" y="71%" width="35%" height="5%" fill="#141D8E" stroke="url(#shadow-effect)" stroke-width="2" rx="5" ry="5"/>
			<text id="text19" class="button-text control numeric-button" x="72%" y="73.8%" style="font-size:14px;font-family:'Brush Script MT' " >Disney+</text>
			
			<!-- Apple TV Button -->
			<rect x="12%" y="78%" width="35%" height="5%" fill="#333" stroke="url(#shadow-effect)" stroke-width="2" rx="5" ry="5"/>
			<text id="text20" class="button-text control numeric-button" x="29.5%" y="80.6%" style="font-size:12px; font-family:'Segoe UI Symbol' ">🍎&#xFE0E tv+</text>
		
			<!-- Roku Channel Button -->
			<rect x="53%" y="78%" width="35%" height="5%" fill="#FFF" stroke="url(#shadow-effect)" stroke-width="3" rx="5" ry="5"/>
			<text id="text21" class="button-text control numeric-button" x="71%" y="79.75%" style="font-size:12px; font-weight:900; fill:#260479 !important;" >Roku</text>
			<text id="text21" class="control button-text numeric-button" x="71%" y="81.6%" style="font-size:8px; font-weight:100; font-style:italic; fill:#260479 !important;" >Channel</text>

            <!-- Custom Buttons - Group B -->
            <circle id="object51" cx="20%" cy="88%" r="3.6%" fill="red" stroke="gray" stroke-width="1"/>
            <text id="text51" class="button-text control numeric-button" x="20.3%" y="88.3%" >A</text>
            
            <circle id="object52" cx="40%" cy="88%" r="3.6%" fill="green" stroke="gray" stroke-width="1"/>
            <text id="text52" class="button-text control numeric-button"x="40.3%" y="88.3%" >B</text>
            
            <circle id="object53" cx="60%" cy="88%" r="3.6%" fill="purple" stroke="gray" stroke-width="1"/>
            <text id="text53" class="button-text control numeric-button" x="60.3%" y="88.3%" >C</text>
            
            <circle id="object54" cx="80%" cy="88%" r="3.6%" fill="blue" stroke="gray" stroke-width="1"/>
            <text id="text54" class="button-text control numeric-button" x="80.3%" y="88.3%" >D</text>
            
        </svg>
    </div>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const numericButtons = document.querySelectorAll('.numeric-button');
            const led1 = document.getElementById('led1');
            const led2 = document.getElementById('led2');
            const buttonData = #buttonData#;
            
            numericButtons.forEach(button => {
                button.addEventListener('click', (event) => {
                    const buttonId = event.target.id; /* Get the ID of the clicked button */
                    console.log("EventListener: ", button)
                    if (buttonId) {
                        sendData(buttonId);
                    }
                });
            });

            function updateButtons() {
                buttonData.forEach(data => {
                    let textId = `text${data.index}`;
                    let textElement = document.getElementById(textId);
                    let objectId = `object${data.index}`;
                    let objectElement = document.getElementById(objectId);

                    if (textElement) {
                        // Modify text content
                        textElement.textContent = data.label;

                        // Modify text color
                        textElement.style.fill = data.tColor;

                        // Hide the Text Content if necessary
                        if (data.bHidden === "true") textElement.style.display = "none";
                        else textElement.style.display = "block";
                    }

                    if (objectElement) {
                        // Modify object background color
                        objectElement.setAttribute("fill", data.bColor);

                        if (data.bHidden === "true") objectElement.style.display = "none";
                        else objectElement.style.display = "block";
                    }
                });
            }

            function sendData(buttonId) {
				/* Trigger a vibration for 200 milliseconds if the user has selected it and it is supported by the device. */
				if (navigator.vibrate && #hapticResponse#) { navigator.vibrate(100); } 

                console.log("Button is: ", buttonId );
                const url = '#URL#';
                /* Only proceed if mode is not null */
                if (buttonId !== null) {
                    led1.classList.add('flicker'); /* Start flickering led1 */
                    // Construct the request payload
                    const payload = { button: buttonId };
                    fetch(url, {
                        mode: 'no-cors',
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    })
                    .then(response => {
                        /* console.log('Data sent:', response); */
                        led1.classList.remove('flicker'); /* Turn off LED1 */
                        led2.setAttribute('fill', 'green'); /* Set led2 to solid green */
                        setTimeout(() => {
                            led2.setAttribute('fill', '#555'); /* Reset led2 to its original color */
                        }, 500); /* Solid for 1 seconds */
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        led1.classList.remove('flicker'); /* Turn off LED1 */
                        led2.setAttribute('fill', 'red'); /* Set led2 to solid red */
                        setTimeout(() => {
                            led2.setAttribute('fill', '#555'); /* Reset led2 to its original color */
                        }, 500); /* Solid for 1 seconds */
                    });
                }
                setTimeout(() => {
                    led1.classList.remove('flicker');
                }, 250);
            }

            /* Initialize buttons */
            updateButtons();
        });
    </script>
</body>
</html>


'''
return HTML

}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End Remote Control APplet Code
//**************
//*******************************************************************************************************************************************************************************************


    

//*******************************************************************************************************************************************************************************************
//**************
//**************  Screen UI and Management Functions
//**************
//*******************************************************************************************************************************************************************************************
//This is the standard button handler that receives the click of any button control.
def appButtonHandler(btn) {
    if (isLogTrace == true) log.trace("<b style='color:green;font-size:medium'>appButtonHandler: Clicked on button: $btn</b>")
    switch (btn) {
        case "rebuildEndpoints":
            createAccessToken()
            state.localEndpoint = "${getFullLocalApiServerUrl()}/tb?access_token=${state.accessToken}"
            state.cloudEndpoint = "${getFullApiServerUrl()}/tb?access_token=${state.accessToken}"
			if (isLogDebug) log.debug ("Endpoints have been rebuilt")
            break
        case "Compile":
            compile()
            break
		case "applyProfile":
			applyProfile()
			compile()
			state.hidden.Display = false
			break
		case "Test":
			test()F
			break
        case "publishRemote":
            publishRemote()
            break
		case 'btnHideDevice':
            state.hidden.Device = state.hidden.Device ? false : true
            break
        case 'btnHideEndpoints':
            state.hidden.Endpoints = state.hidden.Endpoints ? false : true
            break
        case 'btnHideDisplay':
            state.hidden.Display = state.hidden.Display ? false : true
            break
		case 'btnHideCustomize':
            state.hidden.Customize = state.hidden.Customize ? false : true
            break
        case 'btnHidePublish':
            state.hidden.Publish = state.hidden.Publish ? false : true
            break
    }
}

//Save the current HTML to the variable. This is the function that is called by the scheduler.
void publishRemote(){
    if (isLogDebug) log.debug("publishRemote: Entering publishRemote.")
    
    //Test whether we can create a cloud Endpoint to see if OAuth is enabled.
    try {
		if( !state.accessToken ) createAccessToken()
        state.cloudEndpoint = getFullApiServerUrl() + "/tb?access_token=" + state.accessToken
        }
	catch (Exception e){
        if (isLogError) log.error("This app is not OAuth Enabled.  Go to: <b>Developer Tools</b> / <b>Apps Code</b> and open the code for this app.  Click on <b>OAuth</b> and then <b>Enable OAuth in App</b> and leave it athe default values.")
        }
    
    if (isLogEvents) log.debug("publishTable: Remote $myRemote ($myRemoteName) is being refreshed.")
    
    myStorageDevice = parent.getStorageDevice()
    if ( myStorageDevice == null ) {
        if (isLogError) log.error("publishTable: myStorageDevice is null. Is the device created and available? This error can occur immediately upon hub startup. Nothing published.")
        return
    }
    
	if (!state.publish) state.publish = {}
    state.publish.lastPublished = now()
	
	def tileLink1 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe src=" + state.localEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][div]"
	def tileLink2 = "[!--Generated:" + now() + "--][div style='height:100%; width:100%; scrolling:no; overflow:hidden;'][iframe src=" + state.cloudEndpoint + " style='height: 100%; width:100%; border: none; scrolling:no; overflow: hidden;'][/iframe][div]"
		
    myStorageDevice.createTile(settings.myRemote, tileLink1, tileLink2, settings.myRemoteName)
	return
    
}

//Returns a formatted title for a section header based on whether the section is visible or not.
def getSectionTitle(section) {
    if (section == "Introduction") {
        if (state.hidden.Intro == true) return sectionTitle("Introduction ▶") else return sectionTitle("Introduction ▼")
    }
	if (section == "Device") {
        if (state.hidden.Device == true) return sectionTitle("Device ▶") else return sectionTitle("Device ▼")
    }
    if (section == "Endpoints") {
        if (state.hidden.Endpoints == true) return sectionTitle("Endpoints ▶") else return sectionTitle("Endpoints ▼")
    }
    if (section == "Display") {
        if (state.hidden.Display == true) return sectionTitle("Remote Display ▶") else return sectionTitle("Remote Display ▼")
    }
    if (section == "Customize") {
        if (state.hidden.Customize == true) return sectionTitle("Customize Remote ▶") else return sectionTitle("Customize Remote ▼")
    }
    if (section == "Publish") {
        if (state.hidden.Publish == true) return sectionTitle("Publish Remote ▶") else return sectionTitle("Publish Remote ▼")
    }
}

String buttonLink(String btnName, String linkText, int buttonNumber) {
    if (isLogTrace && isLogDetails) log.trace("<b>buttonLink: Entering with $btnName  $linkText  $buttonNumber</b>")
    def myColor, myText
    Integer myFont = 16

    if (buttonNumber == settings.activeButton) myColor = "#00FF00" else myColor = "#000000"
    if (buttonNumber == settings.activeButton) myText = "<b><u>${linkText}</u></b>" else myText = "<b>${linkText}</b>"

    return "<div class='form-group'><input type='hidden' name='${btnName}.type' value='button'></div><div><div class='submitOnChange' onclick='buttonClick(this)' style='color:${myColor};cursor:pointer;font-size:${myFont}px'>${myText}</div></div><input type='hidden' name='settings[$btnName]' value=''>"
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End Screen UI and Management Functions
//**************
//*******************************************************************************************************************************************************************************************


//*******************************************************************************************************************************************************************************************
//**************
//**************  Utility Functions
//**************
//*******************************************************************************************************************************************************************************************

//Returns a string containing the var if it is not null. Used for the controls.
static String bold2(s, var) {
    if (var == null) return "<b>$s (N/A)</b>"
    else return ("<b>$s ($var)</b>")
}

//Functions to enhance text appearance
static String bold(s) { return "<b>$s</b>" }

//Set the Section Titles to a consistent style.
static def sectionTitle(title) { return "<span style='color:#000000; margin-top:1em; font-size:16px; box-shadow: 0px 0px 3px 3px #40b9f2; padding:1px; background:#40b9f2;'><b>${title}</b></span>" }

//Set the body text to a consistent style.
static String body(myBody) { return "<span style='color:#17202A;text-align:left; margin-top:0em; margin-bottom:0em ; font-size:18px'>" + myBody + "</span>&nbsp" }

//Produce a horizontal line of the specified width
static String line(myHeight) { return "<div style='background:#005A9C; height: " + myHeight.toString() + "px; margin-top:0em; margin-bottom:0em ; border: 0;'></div>" }

//Convert [HTML] tags to <HTML> for display.
def toHTML(HTML) {
    if (HTML == null) return ""
    myHTML = HTML.replace("❰", "<")
    myHTML = myHTML.replace("❱", ">")
    return myHTML
}

//Convert <HTML> tags to [HTML] for storage.
def unHTML(HTML) {
    myHTML = HTML.replace("<", "❰")
    myHTML = myHTML.replace(">", "❱")
    return myHTML
}

//Set the notes to a consistent style.
static String summary(myTitle, myText) {
    myTitle = dodgerBlue(myTitle)
    return "<details><summary>" + myTitle + "</summary>" + myText + "</details>"
}

static String dodgerBlue(s) { return '<font color = "DodgerBlue">' + s + '</font>' }

// Get a list of supported commands for a given device and return a sorted list. Adds *toggle if 'on' or 'off' are present.
def getCommandList(thisDevice) {
    if (thisDevice != null) {
		def arithmeticCommands = [ 'setVolume','setLevel', 'setHue', 'setSaturation', 'setPosition', 'setTiltLevel' ]
        def myCommandsList = []
        def supportedCommands = thisDevice.supportedCommands
        supportedCommands.each { command ->
            def commandName = command.name
			if (arithmeticCommands.contains(commandName)) commandName = commandName + "*"
			myCommandsList << commandName
            if (commandName == 'on' || commandName == 'off') { myCommandsList << '*toggle'  }
        }
        
        return myCommandsList.unique().sort()
    }
}

//*******************************************************************************************************************************************************************************************
//**************
//**************  End Utility Functions
//**************
//*******************************************************************************************************************************************************************************************



//*******************************************************************************************************************************************************************************************
//**************
//**************  Standard System Elements
//**************
//*******************************************************************************************************************************************************************************************

//Configures all of the default settings values. This allows us to have some parts of the settings not be visible but still have their values initialized.
//We do this to avoid errors that might occur if a particular setting were referenced but had not been initialized.
def initialize() {
    //if (state.initialized == true) {
      //  if (isLogTrace) log.trace("<b>initialize: Initialize has already been run. Exiting</b>")
        //return
    //}
    log.trace("<b>Running Initialize</b>")

	//Get the settings for the default profile.
	app.updateSetting("selectedProfile", 0)
	data = getProfile()
	
	app.updateSetting("commandsPerLine", 3)
	app.updateSetting("showParameters", [value: "FALSE", type: "enum"])
	
    //Initialze all the Fixed button settings
    for (int i = 1; i <= 21; i++) {
		app.updateSetting("myText$i", data.fixedButtons["button$i"].text.toString() )
		app.updateSetting("myButtonColor$i", "#000000" )
        app.updateSetting("myTextColor$i", [value: "#FFFFFF", type: "color"])
    }
	
	//Initialze all the custom button settings
    for (int i = 51; i <= 54; i++) {
		app.updateSetting("myText$i", data.customButtons["button$i"].text.toString() )
        app.updateSetting("myButtonColor$i", "#000000" )
        app.updateSetting("myTextColor$i", [value: "#FFFFFF", type: "color"])
    }
	
	//Remote Settings
	app.updateSetting("unassignedButtonBehaviour", [value: "Normal", type: "enum"])
	app.updateSetting("enableHapticResponse", [value: "False", type: "enum"])

    //Publishing
	app.updateSetting("publishEndpoints", [value: "Local", type: "enum"])

    //Create Access Points
    createAccessToken()
    state.localEndpoint = "${getFullLocalApiServerUrl()}/tb?access_token=${state.accessToken}"
    state.cloudEndpoint = "${getFullApiServerUrl()}/tb?access_token=${state.accessToken}"
		
	//Set initial Log settings
    app.updateSetting('isLogDebug', false)
    app.updateSetting('isLogError', true)
    app.updateSetting('isLogCommands', false)
    app.updateSetting('isLogConnections', false)
	
	//Have all the sections collapsed to begin with except devices
    state.hidden = [Device: false, Endpoints: true, Display: true, Customize: true, Publish: true]
    
    state.initialized = true
}

def updated(){
    if(!state?.isInstalled) { state?.isInstalled = true }
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  End Standard System Elements
//**************
//*******************************************************************************************************************************************************************************************


