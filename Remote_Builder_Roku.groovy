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
*  Gary Milne - September 25th, 2024 @ 1:17PM PM
*
*  Version 1.1.3 - Initial Public Release based on Remote Builder TV skeleton version 1.1.3
*  Version 2.0.0 - Converted everything from SVG to HTML\CSS for simplified layout. Adopted Materials Symbols for the Icon Font. Added publishing of Remote info to Remote Builder Parent for future remote sharing.
*
**/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field

static def buttonGroup() { return  }

@Field static final codeDescription = "<b>Remote Builder - Roku 2.0.0 (9/25/24)</b>"
@Field static final codeVersion = 200
@Field static final moduleName = "Roku Remote"

def deviceProfileList() { return [0:'Roku Connect (>= 2.8.2) by Armand Welsh'] }

definition(
	    name: "Remote Builder - Roku",
        description: "Generates a TV remote control that can operate be executed from a web browser or embedded into a Hubitat Dashboard.",
        importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Roku2.groovy",
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
				//Add Google Materials Symbols Support
				paragraph "<link rel='stylesheet' href='https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200' />"
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
						myIcon = data.fixedButtons["button${index}"].iconName
						
						if (myIcon != null) myIconHTML = "<span class='material-symbols-outlined' style='font-size: 24px;vertical-align: bottom;'>" + myIcon.toString() + "</span>"
						else myIconHTML = data.fixedButtons["button${index}"].text
						input ("myCommand$i", "enum", title: "&nbsp<b>Button</b> (" + myIconHTML + ")", options: getCommandList(settings["myRoku"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")	
						
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
			//myIcon = data.fixedButtons["button${index}"].iconName
			fixedButtons = [
				button1: [text: "Power", command: "keyPress", parameter: "Power", iconName: "power_settings_new"],
 				button2: [text: "⬅", command: "keyPress", parameter: "Back", iconName: "keyboard_backspace"],
				button3: [text: "🏠︎", command: "home", parameter: "?", iconName: "home"],
 				button4: [text: "▲", command: "keyPress", parameter: "Up", iconName: "keyboard_arrow_up"],
				button5: [text: "◀", command: "keyPress", parameter: "Left", iconName: "keyboard_arrow_left"],
				button6: [text: "▶", command: "keyPress", parameter: "Right", iconName: "keyboard_arrow_right"],
 				button7: [text: "▼", command: "keyPress", parameter: "Down", iconName: "keyboard_arrow_down"],
 				button8: [text: "OK", command: "keyPress", parameter: "Select", iconName: "keyboard_return"],
				
				button9: [text: "↺", command: "keyPress", parameter: "InstantReplay", iconName: "replay"],
				button10: [text: "📺 ", command: "keyPress", parameter: "InputTuner", iconName: "live_tv"],
				button11: [text: "*", command: "keyPress", parameter: "Info", iconName: "asterisk"],
				
				button12: [text: "◀◀", command: "keyPress", parameter: "Rev", iconName: "fast_rewind"],
 				button13: [text: "▶ \\ ❚❚", command: "play", parameter: "?", iconName: "play_arrow"],
 				button14: [text: "▶▶", command: "keyPress", parameter: "Fwd", iconName: "fast_forward"],
				
				button15: [text: "🔉", command: "volumeDown", parameter: "?", iconName: "volume_down"],
 				button16: [text: "🔇", command: "mute", parameter: "?", iconName: "volume_mute"],
				button17: [text: "🔊", command: "volumeUp", parameter: "?", iconName: "volume_up"],
 				
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
    localContent = localContent.replace("#url#", state.localEndpoint)
    
	def cloudContent = content
	cloudContent = cloudContent.replace('#connectionIcon#', '☁︎' )
    cloudContent = cloudContent.replace("#url#", state.cloudEndpoint )
		
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
    log.info ("Compiled Remote: Before is: " + String.format("%,d", initialSize) + " - After is: " + String.format("%,d", input.size()) + " bytes.")
	
	
			
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
	def myResult 
    
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
	
	// Check for missing device or command, return HTTP 400
    if (myDevice == null || myCommand == null) {
        log.error("Device or command is missing. Cannot execute.")
        render status: 400, contentType: 'application/json', data: [error: 'Device: $myDevice or Command: $myCommand is missing.']
        return
    }
	
	result = assembleCommand(myCommand, myParameter)
	if (isLogDebug) log.info ("Command Array is: $result")
    
	//If the values are valid we will execute the command    	
	switch(myCommand){
		case ["setLevel*"]:
			myLevel = myDevice.currentValue('level')
			def newValue = getNewValue( myLevel, result.parameters[0] )
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
			if (myDevice.currentValue('switch') == 'on') { myResult = myDevice.offX() }
			else myResult = myDevice.onX()
			//log.info ("myResult is: $myResult ")
            return
        default:
			if (myDevice != null && myCommand != null ) {
				if (myParameter == null || myParameter == "?" || myParameter == "" ) myDevice."${result.command}"()
				if (result.parameterCount == 1 ) myResult = myDevice."${result.command}"( result.parameters[0] )
				if (result.parameterCount == 2 ) myResult = myDevice."${result.command}"( result.parameters[0], result.parameters[1] )
				if (result.parameterCount == 3 ) myResult = myDevice."${result.command}"( result.parameters[0], result.parameters[1], result.parameters[2] )
			}
		//log.info ("myResult is: $myResult ")
		result = render status: 200, contentType: 'application/json', data: '{"success": "Command executed successfully"}'
   		return result
			
		/* myResult is null if the command does not exist and an 8 digit number if the command exists and is executed. */
		/* Future: Change the logic to get a result from each execution and either return status 200 or status 400 to indicate success or failure. */
		}
}

//Takes the existing value of the attribute and the parameter.  If the parameter has a leading +,- or * then the existing value will be adjusted by that much. If not then the value will be set to the integer value of the specified parameter.
def getNewValue(oldValue, parameter) {
    if (isLogDebug) log.debug("newValue: $oldValue , $parameter")
	if (oldValue == null ) {
		log.warn("Warning: Unable to get existing value to perform arithmetic modification. Executing command with value of 0.")
		return 0 
	}
	
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
	if (isLogDebug) log.info ("assembleCommands: '$myCommand' -  '$myParameters' ")
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
    if( isLocal ) { 
		result = render contentType: "text/html;charset=UTF-8", data:state.compiledLocal, status:200  
	}
	
    if( isCloud ) { 
		result = render contentType: "text/html;charset=UTF-8", data:state.compiledCloud, status:200  
	}
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
    <title>HTML Roku Remote</title>
	<!-- Include Material Symbols Font -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />
	
    <style>
		:root { --myGradient: linear-gradient(to bottom, rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.7)); }
	
		body, html {
			align-items: center;
			background-color: transparent;
			display: flex;
			height: 100%;
			justify-content: center;
			margin: 0;
			padding:0;
			color: white;
		}

		/* Style for the remote rectangle */
		.rectangle {
			background-color: #333;
			border: 1vh solid #222;
			border-radius: 6vh;
			height: 90vh;
			position: relative;
			width: 25vh;
		}

		/* Start of Roku purple cross */
		/* This is the rectangle effect behind the keypad */
		.rounded-rect {
			aspect-ratio: 1 / 1; /* This makes the height equal to the width, creating a square */
			background: radial-gradient(circle at center, #444 20%, #333 50%, #444 80%); /* Mimicking radial gradient */
			border: 1px solid #444; 
			border-radius: 40%; 
			left: 50%; 
			opacity: 0.5;
			position: absolute;
			top: 33.3%; 
			width: 80%;
		}
		
		/* These are the classes for the Roku purple cross */
		.rect-common {
			position: absolute;
			top: 33.3%;
			left: 50%;
			background-color: #724AFF;
			border: 0px solid red;
			border-radius: 2vh;
		}

		.rect-horizontal {width: 60%; height: 6%;}
		.rect-vertical {width: 20%; height: 18%;}
		
		/* End of Roku purple cross */

		/*Set the properties for any materials symbol used on the remote */
		.material-symbols-outlined {
			font-size: clamp(4px, 3vh, 40px) !important;
			font-variation-settings: 'FILL' 1,'wght' 400,'GRAD' 0;
		}

		/* Base button class with shared properties */		
		.button {
			display: flex;
			color: white;
			background-color: #333; /* Solid background for the button */
			border-radius: 5vh; /* Rounded corners */
			cursor: pointer;
			height: 5%;
			padding: 0px;
			position: absolute;
			width: 30%;
			z-index: 2; /* Ensure it's clickable on top of other elements */
		}

		/* Standard visible button */
		.button-normal{
			background-clip: padding-box, border-box; /* Clip background to show gradient on border */
			background-image: linear-gradient(to bottom, rgba(128, 128, 128, 0.7) 0%, rgba(30, 30, 30, 0.8) 50%, rgba(128, 128, 128, 0.7) 100%),
							  linear-gradient(to bottom, rgba(0, 0, 0, 0.7) 0%, rgba(255, 255, 255, 1) 50%, rgba(0, 0, 0, 0.3) 100%); /* Gradient for border */
			background-origin: border-box;
			border: 0.2vh solid transparent; /* Border will be transparent initially */
		}

		/* Transparent button - Used for the navigation pad buttons */
		.button-transparent {
			background-color: transparent; /* Make the background fully transparent */
			background-image: none; /* Remove background image from .button */
			border: transparent; /* Remove any border */
			width: 15% !important;
		}
		
		/* These are the dedicated Netflix Disney Apple TV and Roku buttons */
		.feature-button {
			font-weight: bold;
			height: 5%;
			width: 40%;
			border-radius:1.5vh;
		}

		/* Gradient used on feature buttons */
		.gradient {
			background-clip: padding-box, border-box;
			background-image: linear-gradient(white, white), var(--myGradient);
			background-origin: border-box;
		}
		
		/* Start of Feature Buttons */
		.netflix {
			color: red;
			letter-spacing: -0.5px;
			text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.7);
		}

		.disney {
			background-image: linear-gradient(#0F0E46, #3161BD), var(--myGradient);
			color: white;
			font-style: italic;
			font-weight: 200;
		}

		.appletv {
			background-image: linear-gradient(#2F2F2F, #131313), var(--myGradient);
			color: white;
			font-family: 'Segoe UI Symbol';
			font-weight: 200;
		}

		.roku {
			background-image: linear-gradient(white, white), var(--myGradient);
			color: #260479;
			display: flex;
			flex-direction: column;
			font-weight: 900;
		}
		/* End of Feature Buttons */
		
		/* Custom Buttons */
		.custom {
			aspect-ratio: 1 / 1; /* This makes the height equal to the width, creating a square */
			width:4.25vh;
			border-radius: 50%;
			border: 1px solid white;
		}
				
		/* Start of general text formatting classes */
		.dynamic-font {
			font-size: clamp(4px, 2vh, 40px) !important; /* Dynamic font size */
		}
		.dynamic-font-large .material-symbols-outlined {
			font-size: clamp(4px, 4vh, 60px) !important; /* Dynamic font size */
		}	

		/* Standard Text Elelement */
		.text-element {
            position: absolute;
            font-size: 4vh;
            color: #555;
        }
		
		/* Used to center all objects that need it */
		.center {
			align-items: center;
			justify-content: center; 
			text-align: center;
			transform: translate(-50%, -50%);
		}
		
		/* End of general text formatting classes */
		
		/* Animation */
        .flicker {animation:flickerOrange 0.25s linear forwards}  
        @keyframes flickerOrange {0%, 20%, 40%, 60%, 80%, 100% {color:#555;} 10%, 30%, 50%, 70%, 90% {color:orange;} }
		
		</style>

</head>
<body>

	<div class="rectangle" style="display:flex; justify-content:center; align-items:center; height:90vh; overflow:hidden;">
		<!-- Local\\Cloud Indicator -->	
        <div class="text-element center" style="left:50%; top:4%;" >#connectionIcon#</div>
		
		<div id="led1" class="text-element center" style="left:15%; top:5%">⬤</div>
		<div id="led2" class="text-element center" style="left:85%; top:5%">⬤</div>

		<div class="rectangle rect-common rect-horizontal center"></div>
        <div class="rectangle rect-common rect-vertical center"></div>
		
		<div class="rounded-rect center"></div>

		<!-- Customize the Power Button to make it round rather than rectangular -->
        <button id="text1" class="myButtons button button-normal center dynamic-font-large" style="left:50%; top:10%; width:6vh !important; height:6vh !important; border-radius: 50% !important;"><span class="material-symbols-outlined" style="color:red;">power_settings_new</span></button>	
		<button id="text2" class="myButtons button button-normal center dynamic-font-large" style="left:25%; top:18%;"><span class="material-symbols-outlined" >arrow_left_alt</span></button>	
		<button id="text3" class="myButtons button button-normal center" style="left:75%; top:18%;"><span class="material-symbols-outlined" >home</span></button>	
		
		<!-- Navigation Pad -->
		<button id="text4" class="button button-transparent center" style="left:50%; top:26.5%;"><span class="material-symbols-outlined" style="transform: rotate(-90deg);" >arrow_forward_ios</span></button>	
		<button id="text5" class="button button-transparent center" style="left:30%; top:33.4%;"><span class="material-symbols-outlined" >arrow_back_ios</span></button>	
		<button id="text6" class="button button-transparent center" style="left:73%; top:33.4%;"><span class="material-symbols-outlined" >arrow_forward_ios</span></button>	
		<button id="text7" class="button button-transparent center" style="left:50%; top:40%;"><span class="material-symbols-outlined" style="transform: rotate(90deg);" >arrow_forward_ios</span></button>	
		<button id="text8" class="button button-normal center dynamic-font" style="left:50%; top:33.4%; width:5vh !important; height:5vh !important; border-radius: 50% !important; opacity:60%">OK</button>
		
		<button id="text9" class="button button-normal center" style="left:20%; top:50%; width:25%"><span class="material-symbols-outlined" >replay</span></button>
		<button id="text10" class="button button-normal center" style="left:50%; top:50%; width:25%"><span class="material-symbols-outlined" >live_tv</span></button>
		<button id="text11" class="button button-normal center" style="left:80%; top:50%; width:25%"><span class="material-symbols-outlined" >emergency</span></button>
		
		<button id="text12" class="button button-normal center" style="left:20%; top:58%; width:25%"><span class="material-symbols-outlined" >fast_rewind</span></button>
		<button id="text13" class="button button-normal center dynamic-font-large" style="left:50%; top:58%; width:30%"><span class="material-symbols-outlined" >play_pause</span></button>
		<button id="text14" class="button button-normal center" style="left:80%; top:58%; width:25%"><span class="material-symbols-outlined" >fast_forward</span></button>
		
		<button id="text15" class="button button-normal center" style="left:20%; top:66%; width:25%"><span class="material-symbols-outlined" >volume_down</span></button>
		<button id="text16" class="button button-normal center" style="left:50%; top:66%; width:30%"><span class="material-symbols-outlined" >volume_off</span></button>
		<button id="text17" class="button button-normal center" style="left:80%; top:66%; width:25%"><span class="material-symbols-outlined" >volume_up</span></button>
		
		<!-- Feature Buttons -->
		<button id="text18" class="button feature-button gradient center dynamic-font netflix" style="left:28%; top:75%">NETFLIX</button>
		<button id="text19" class="button feature-button gradient center dynamic-font disney" style="left:72%; top:75%">Disney+</button>
		<button id="text20" class="button feature-button gradient center dynamic-font appletv" style="left:28%; top:82%">🍎 tv+</button>	
		<button id="text21" class="button feature-button gradient center dynamic-font roku" style="left:72%; top:82%; ">
			<span style="line-height: 1;"><i>Roku</i></span>
			<span style="font-size:50%; font-weight:100; font-style:italic; color:#000; line-height:1;">Channel</span>
		</button>
		
		<!-- Custom Buttons -->
        <button id="text51" class="button custom center dynamic-font" style="left:20%; top:90%; background-color:red;" >A</button>
		<button id="text52" class="button custom center dynamic-font" style="left:40%; top:90%; background-color:green;" >B</button>
		<button id="text53" class="button custom center dynamic-font" style="left:60%; top:90%; background-color:purple;" >C</button>
		<button id="text54" class="button custom center dynamic-font" style="left:80%; top:90%; background-color:blue;" >D</button>
	
    </div>
		
	    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const myButtons = document.querySelectorAll('.button');
            const led1 = document.getElementById('led1');
            const led2 = document.getElementById('led2');
			const buttonData = #buttonData#;
            
            myButtons.forEach(button => {
                button.addEventListener('click', (event) => {
                    const buttonId = event.currentTarget.id; /* Get the ID of the clicked button */
                    /*console.log("EventListener: ", button)*/
                    if (buttonId) {
                        sendData(buttonId);
                    }
                });
            });

            function updateButtons() {
                buttonData.forEach(data => {
                    let textId = `text${data.index}`;
                    let textElement = document.getElementById(textId);
                    
                    if (textElement) {
                        /* Modify text content, text color and background */
                        textElement.textContent = data.label;
                        textElement.style.color = data.tColor;
                        textElement.style.backgroundColor = data.bColor;
                        /* Hide the Text Content if necessary */
                        if (data.bHidden === "true") textElement.style.display = "none";
                        else textElement.style.display = "block";
                    }
                });
            }

			function sendData(buttonId) {
				/* Trigger vibration if supported */
				if (navigator.vibrate && #hapticResponse#) { navigator.vibrate(100); }

				const url = '#url#';

				if (buttonId !== null) {
					led1.classList.add('flicker');  /* Start flickering led1 */

					/* Construct the request payload */
					const payload = { button: buttonId };
					
					/* A successful execution will come back with status 200 */
					fetch(url, {
						method: 'POST',
						headers: { 'Content-Type': 'application/json' },
						body: JSON.stringify(payload)
					})
					.then(response => {
						/* Check if the status code is 200 */
						if (response.status !== 200) { throw new Error('Unexpected response status: ' + response.status); }
						return response.json();  /* Parse the response body as JSON */
					})
					.then(data => {
						console.log('Server response:', data);  /* This contains the success message or any other data */
						led1.classList.remove('flicker');  /* Stop flickering led1 */
						led2.style.color = 'green';  /* Set led2 to solid green */
						setTimeout(() => {
							led2.style.color = '#555';  /* Reset led2 to its original color */
						}, 1000);
					})
					.catch(error => {
						console.error('Error:', error);
						led1.classList.remove('flicker');  /* Turn off led1 flicker */
						led2.style.color = 'red';  /* Set led2 to red */
						setTimeout(() => {
							led2.style.color = '#555';  /* Reset led2 to its original color */
						}, 1000);
					});
				}

				setTimeout(() => {
					led1.classList.remove('flicker');  /* Ensure led1 flicker stops */
				}, 1000);
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
	
	//Publish the Remotes to the parent for Remote Links function.
	def inputMap = [ number: myRemote.toString(), name: myRemoteName, localEndpoint: state.localEndpoint, cloudEndpoint: state.cloudEndpoint ]
	parent.saveRemoteLinks(inputMap)
	
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


