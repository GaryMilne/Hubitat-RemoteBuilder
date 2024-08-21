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
*  Remote Builder TV Remote - ChangeLog
*
*  Gary Milne - August 20th, 2024 @ 9:15 PM
*
*  Version 1.0.0 - Limited Release
*  Version 1.0.1 - Removed Install on Open and added OAuth.
*  Version 1.0.2 - Added the ability to provide parameters in the command array using the # as a separator.
*  Version 1.0.3 - Added a profile for the MolSmart - GW3 - TV (irweb) driver.
*
**/

/* Possible Todo's
Add mode device profiles
Load commands from the device
Allow users to select a command with a parameter to assign to a button.
*/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field

static def buttonGroup() { return  }

@Field static final codeDescription = "<b>Remote Builder - TV 1.0.3 (8/20/24)</b>"
@Field static final codeVersion = 103
@Field static final moduleName = "TV Remote"

def deviceProfileList() { return [0:'Samsung TV Remote (4.1-2) by David Gutheinz', 1:'MolSmart - GW3 - TV (irweb) by VH'] }

definition(
	    name: "Remote Builder - TV",
        description: "Generates a TV remote control that can operate be executed from a web browser or embedded into a Hubitat Dashboard.",
        importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_TV.groovy",
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
				paragraph "<b>Select the target TV and the Device Driver Profile.</b> Only devices with the capability 'samsungTV' will be listed. "
				input ("myTV", "capability.samsungTV", title: "<b>Default TV Device</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px") /* top right bottom left */
				if (myTV != null ) { 
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
            	myText += "Both endpoints can be active at the same time and can be enabled or disable through this interface.<br>"
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
				
                input(name: "selectedButtonGroup", type: "enum", title: bold("Select Button Group"), options: ['FIXED', 'CUSTOM'], required: true, defaultValue: "FIXED", submitOnChange: true, width: 3)
				
				startIndex = 1
				endIndex = fixedButtonCount
				if(selectedButtonGroup == "CUSTOM") { startIndex = 51; endIndex = startIndex + customButtonCount - 1 }
                paragraph line(1)
				
                (startIndex..endIndex).each { i ->
					if (i > 50) prefix = "Custom"
                    if (i > 50) input ("myDevice$i", "capability.*", title: "<b>$prefix Button " + ( i - 50 ) + " Device</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px") /* top right bottom left */
					if (i < 50) input ("myCommand$i", "enum", title: "&nbsp<b>Command</b> (" + data.fixedButtonText[i - 1].toString() + ")", options: getCommandList(settings["myTV"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					if (i > 50) input ("myCommand$i", "enum", title: "&nbsp<b>Command</b>", options: getCommandList(settings["myDevice$i"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
                    if (i > 50) input ("myButtonColor$i", "color", title: "&nbsp<b>Button Color</b>", defaultValue: "#FFFFFF", required: false, width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
					if (i > 50) input ("myText$i", "text", title: "&nbsp<b>Character</b>", multiple: false, submitOnChange: true, width: 1, required: true, style: "margin: 2px 10px 2px 10px; padding:3px;border: 1px solid gray")
                    if (i > 50) input ("myTextColor$i", "color", title: bold("&nbsp<b>Text Color</b>"), required: false, defaultValue: "#FFFFFF", width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
                    if (i > 50) paragraph line(1)
                } 
				input(name: "unassignedButtonBehaviour", type: "enum", title: bold("Unassigned Button Behaviour"), options: ["Normal", "Disabled", "Hidden"], required: false, defaultValue: "Normal", submitOnChange: true, width: 2, newLine: true, style:"margin-right: 20px")
            }
        
        //Start of Publish Section
		section(hideable: true, hidden: state.hidden.Publish, title: buttonLink('btnHidePublish', getSectionTitle("Publish"), 20)) {
            input(name: "myRemote", title: "<b>Attribute to store the Remote? (Optional)</b>", type: "enum", options: parent.allTileList(), required: false, submitOnChange: true, width: 3, defaultValue: 0, newLine: false)
            input(name: "myRemoteName", type: "text", title: "<b>Name this Remote</b>", submitOnChange: true, width: 3, defaultValue: "New Remote", newLine: false, required: true)
            input(name: "tilesAlreadyInUse", type: "enum", title: bold("For Reference Only: Remotes in Use"), options: parent.getTileList(), required: false, defaultValue: "Remotes List", submitOnChange: true, width: 3)
                                    
            if (myRemoteName) app.updateLabel(myRemoteName)
            myText =  "Publishing a remote is optional and only required if it will be used within a dashboard. Remotes can be accessed directly via the URL's in the Endpoints section and bypass the Dashboard entirely. The <b>Remote Name</b> given here will also be used as the name for this instance of Remote Builder. "
			myText += "Appending the name with your chosen remote number can make parent display more readable.<br>"
            myText += "Only links to the Local and Cloud Endpoints are stored in the Remote Builder Storage Device when publishing is enabled.<br>"
            paragraph myText
			
            paragraph summary("Publishing Notes", myText)																																																																														 
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
				input(name: "isLogConnections", type: "bool", title: "<b>Record All Connection Requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogActions", type: "bool", title: "<b>Record All Action Requests?</b>", defaultValue: true, submitOnChange: true, width: 3)
				input(name: "rebuildEndpoints", type: "button", title: "<b>Rebuild Endpoint(s)</b>", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, newLine:true)
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
	
	def fixedButtonText = []
	def fixedButtonCommands = []
	def fixedButtonCount = 0
	//You can execute a command with parameters. Simply separate the command and the paramters with a # symbol, for example: "myCommand#myParameter1#myParameter2"
	def customButtonCommands = []
	def customButtonColor = []
	def customButtonText = []
	def customButtonTextColor = []		
	def customButtonCount = 0
	
	if (isLogDebug) log.debug ("Selected Profile is: " + selectedProfile.toInteger() )
	
	switch(selectedProfile.toInteger()){
        case [0]: /* Samsung TV Remote */
			fixedButtonText = ["⚡️", "❖", "▲", "▼", "◀", "▶", "OK", "▲", "▼", "🔇", "☰", "▲", "▼", "↩", "⌂", "⚙️", "◀◀" , "▶ \\ ❚❚", "▶▶" ]
			fixedButtonCommands = ["on", "source", "arrowUp", "arrowDown", "arrowLeft", "arrowRight", "enter", "volumeUp", "volumeDown", "mute", "guide", "channelUp", "channelDown", "exit", "home", "menu", "fastBack", "play", "fastForward" ]
			fixedButtonCount = fixedButtonCommands.size()
			//You can execute a command with parameters. Simply separate the command and the paramters with a # symbol, for example: "myCommand#myParameter1#myParameter2"
			customButtonCommands = ["off", "channelList", "appRunNetflix", "appRunPrimeVideo","appOpenByName#Disney+", "appRunYouTube", "exit", "exit", "exit", "exit" ]
			customButtonColor = ["#555555", "#555555", "#FFFFFF", "#1294F7","#142156", "#FF0000", "#FF0000", "#FFA500", "#0000FF", "#008000" ]
			customButtonText = ["◆︎", "◧", "N", "A", "𝒟", "▶", "1", "2", "3", "4" ]
			customButtonTextColor = ["#FFFFFF", "#FFFFFF","#FF0000", "#FFFFFF","#F3ECFE", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF" ]		
			customButtonCount = customButtonCommands.size()
			break
		    
        case [1]: /* MolSmart Default*/
			fixedButtonText = ["⚡️", "❖", "▲", "▼", "◀", "▶", "OK", "▲", "▼", "🔇", "☰", "▲", "▼", "↩", "⌂", "⚙️", "◀◀" , "▶ \\ ❚❚", "▶▶" ]
			fixedButtonCommands = ["poweron", "source", "up", "down", "left", "right", "confirm", "volumeUp", "volumeDown", "mute", "guideIRsend", "channelUp", "channelDown", "exit", "home", "menu", "backIRsend", "playIRsend", "nextIRsend" ]
			fixedButtonCount = fixedButtonCommands.size()
		
			//You can execute a command with parameters. Simply separate the command and the paramters with a # symbol, for example: "myCommand#myParameter1#myParameter2"
			customButtonCommands = ["poweroff","infoIRsend", "appNetflix", "appAmazonPrime", "", "appYouTube", "btnextra1", "btnextra2", "btnextra3", "btnextra4" ]
			customButtonColor = ["#555555", "#555555", "#FFFFFF", "#1294F7","#142156", "#FF0000", "#FF0000", "#FFA500", "#0000FF", "#008000" ]
			customButtonText = ["◆︎", "◧", "N", "A", "𝒟", "▶", "1", "2", "3", "4" ]
			customButtonTextColor = ["#FFFFFF", "#FFFFFF","#FF0000", "#FFFFFF","#F3ECFE", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF", "#FFFFFF" ]		
			customButtonCount = customButtonCommands.size()
			break
		
        default:
            return [ "No Device Profile Found"]
			}
	
	
	buttonData = [ fixedButtonText: fixedButtonText, fixedButtonCommands: fixedButtonCommands, fixedButtonCount: fixedButtonCount, customButtonCommands: customButtonCommands, customButtonColor: customButtonColor, \
					customButtonText: customButtonText, customButtonTextColor: customButtonTextColor, customButtonCount: customButtonCount ]
	if (isLogDebug) log.debug ("Data is: $buttonData")		   
	return buttonData
}


//Sets the default actions for each of the buttons.
def applyProfile(){
	
	def data = getProfile()
	log.info("Data is: $data")
	def fixedButtonCount = data.fixedButtonCount
	def customButtonCount = data.customButtonCount + 51 - 1
	
	// Loop from 1 to fixedButtonCount and assign each buttonCommand to the appropriate myCommand setting.
	(1..fixedButtonCount).each { index ->
    	def command = data.fixedButtonCommands[index - 1]
		app.updateSetting("myCommand${index}", [value:"$command", type:"enum"])
		//log.info("myCommand${index} is: $command ")
	}	
	
	//Custom buttons start at 51. These are the buttons that can be customized.
	(51..customButtonCount).each { deviceIndex ->
		index = deviceIndex - 51
		log.info ("Device Index is: $deviceIndex and index is: $index")
		app.updateSetting("myDevice${deviceIndex}", [type: "capability", value: settings.myTV ] )
		
		log.info("<b>Button " + (index + 1) + "($deviceIndex)</b>")
		def myCustomButtonColor = data.customButtonColor[index]
		log.info ("Button Color: $myCustomButtonColor")
		app.updateSetting("myButtonColor${deviceIndex}", [value: "$myCustomButtonColor", type: "color"])
		
		def myCustomText = data.customButtonText[index]
		log.info ("Text: $myCustomText")
		app.updateSetting("myText${deviceIndex}", [value: "$myCustomText", type: "text"])
		
		def myCustomTextColor = data.customButtonTextColor[index]
		log.info ("Text Color: $myCustomTextColor")
		app.updateSetting("myTextColor${deviceIndex}", [value: "$myCustomTextColor", type: "color"])
		
		log.info("myDevice$index: " + settings."myDevice${deviceIndex}" )
		def myCommand = data.customButtonCommands[index]
		log.info("Command is: $myCommand ")
		app.updateSetting("myCommand${deviceIndex}", [value:"$myCommand", type:"enum"])
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
    (51..60).each { i ->
        //def index = (i)
		isActive = true
		isHidden = false
		def myCommand = settings."myCommand$i"
		def myDevice = settings."myDevice$i"
		
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
	
	if (i < 20) myDevice = settings["myTV"]
	else myDevice = settings["myDevice$i"]
    myCommand = settings["myCommand$i"]
	
	// Record the action request
    if (isLogActions) log.info ( "Remote Builder Data Received - Remote: $myRemote - Name: $myRemoteName - Button: $i - Device: $myDevice - Command: $myCommand")
	
	if (myDevice == null || myCommand == null) return
	
    def myCommandIndex = 0
    if (myCommand && myCommand[-1] in '1'..'4') {
        myCommandIndex = myCommand[-1] as int
    }
	
	result = parseCommandString(myCommand.toString())
	if (isLogDebug) log.info ("Command String is: $result")
	
	//If the values are valid we will execute the command    	
	switch(myCommand){
        case ["*toggle"]:
			if (myDevice.currentValue('switch') == 'on') { myDevice.off() }
			else myDevice.on()
            return
        case ["*push1","*push2","*push3","*push4"]:
			myDevice.push(myCommandIndex)
            return
		case ["*doubleTap1","*doubleTap2","*doubleTap3","*doubleTap4"]:
			myDevice.doubleTap(myCommandIndex)
            return
        default:
			if (myDevice != null && myCommand != null ) {
				if (result.parameterCount == 0 ) myDevice."${result.command}"()
				if (result.parameterCount == 1 ) myDevice."${result.command}"( result.parameters[0] )
				if (result.parameterCount == 2 ) myDevice."${result.command}"( result.parameters[0], result.parameters[1] )
			}
			return
	}
}

// Parse out the command string
def parseCommandString(String commandString) {
    def result = [command: "", parameters: [], parameterCount: 0]
    
    // Split the string by the '#' character
    def parts = commandString.split('#')
    
    // Ensure that there is at least a command present
    if (parts.size() > 0) {
        // The first part is the command
        result['command'] = parts[0]
        
        // Check if there are additional parts (parameters)
        if (parts.size() > 1) {
            // The rest are the parameters
            result['parameters'] = parts[1..-1]
            result['parameterCount'] = result['parameters'].size()
        }
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
    <title>Remote Control</title>
    <style> 
        html, body {touch-action: manipulation; font-family: Arial, sans-serif, "Segoe UI Symbol"; overflow:hidden;}
		.button-text { font-family: Arial; font-size: 18px; fill: white; text-anchor: middle; dominant-baseline: middle; -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; }
		.button-text-small { font-size:12px;}
		.button-text-large { font-size:24px;}		
		.control { cursor:pointer; }
        .no-cursor { font-size:18px; font-weight:bold; pointer-events:none; text-anchor:middle; }
		.force-font {font-family: "Courier New"; }  /* This class is used to force the use of non-eMojoi characters as these get presented on an iPad at times */
		
        /* Animation */
        .flicker {animation:flickerOrange 0.25s linear forwards}  
        @keyframes flickerOrange {0%, 20%, 40%, 60%, 80%, 100% {fill:#555} 10%, 30%, 50%, 70%, 90% {fill:orange}}
    </style>
</head>
<body>
    <div class="container" style="display:flex; justify-content:center; align-items:center; height:98vh; overflow:hidden;">
        <svg id="remote" viewBox="0 0 180 420" preserveAspectRatio="xMidYMid meet" style="width:100%; height:90%">
            <!-- Define the radial gradient for the shadow effect -->
            <defs>
                <linearGradient id="shadow-effect" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" stop-color="white" stop-opacity="0.2" />
                    <stop offset="100%" stop-color="black" stop-opacity="0.7" />
                </linearGradient>
                <linearGradient id="vertical-gradient" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="20%" stop-color="#555" />
                    <stop offset="80%" stop-color="#111" />
                </linearGradient>
            </defs>
            
            <!-- Remote body -->
            <rect x="10" y="10" width="160" height="400" rx="20" ry="20" fill="#333" stroke="#222" stroke-width="5" />

            <!-- Power button -->
            <circle cx="40" cy="40" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text1" class="numeric-button control button-text" x="40" y="40" >⚡</text>
            
            <!-- LED -->
            <circle id="led1" cx="40%" cy="7%" r="2.5%" fill="#555"/>
            <circle id="led2" cx="60%" cy="7%" r="2.5%" fill="#555"/>
            
            <!-- Local\\Cloud Indicator -->
            <text class="no-cursor" id="connection-text" x="50%" y="10%" fill="#fff">#connectionIcon#</text>
            
            <!-- Source Button -->
            <circle cx="140" cy="40" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text2" class="button-text control numeric-button" x="140" y="42" style="font-size:30px">❖</text>
            
            <!-- Navigation buttons -->
            <circle cx="90" cy="120" r="50" fill="#555" stroke="url(#shadow-effect)" />
            <text id="text3" class="button-text control numeric-button" x="90" y="85" >▲</text>
            <text id="text4" class="button-text control numeric-button" x="90" y="155" >▼</text>
            <!-- The rotations on the following lines are required for these left right arrows to display properly on an iPad -->
            <text id="text5" class="button-text control numeric-button" x="52" y="120" transform="rotate(-90, 52, 120)">▲</text>
            <text id="text6" class="button-text control numeric-button" x="128" y="120" transform="rotate(90, 128, 120)">▲</text>
            
            <!-- OK button -->
            <circle cx="90" cy="120" r="15" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" />
            <text id="text7" class="control numeric-button button-text button-text-small" x="90" y="122" >OK</text>
                
            <!-- Volume Controls -->
            <rect x="20" y="162.5" width="30" height="70" rx="20" ry="20" fill="#333" stroke="url(#shadow-effect)" stroke-width="1" />
            <text id="text8"  class="button-text control numeric-button" x="35" y="180" >▲</text>
            <text class="button-text button-text-small" x="35" y="200" >VOL</text>
            <text id="text9"  class="button-text control numeric-button" x="35" y="220">▼</text>

            <!-- Mute button -->
            <circle cx="70" cy="200" r="15" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text10" class="button-text control numeric-button" x="70" y="200">🔇</text>  

            <!-- Guide button -->
            <circle cx="110" cy="200" r="15" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text11" class="button-text control numeric-button" x="110" y="200">☰</text>  

            <!-- Channel Controls -->
            <rect x="130" y="162.5" width="30" height="70" rx="20" ry="20" fill="#333" stroke="url(#shadow-effect)" stroke-width="1" />
            <text id="text12" class="button-text control numeric-button" x="145" y="180" >▲</text>
            <text class="button-text button-text-small" x="145" y="200" >CH.</text>
            <text id="text13" class="button-text control numeric-button" x="145" y="220" >▼</text>

            <!-- Back button -->
            <circle cx="40" cy="267" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text14"  x="40" y="270" class="control numeric-button button-text force-font" style="font-size:30px; font-weight:900">↩</text>

            <!-- Home button -->
            <circle cx="90" cy="267" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text15" x="90" y="267" class="control numeric-button button-text" fill="#FFF" style="font-size:30px; font-weight:900">⌂</text>
            
            <!-- Gear button -->
            <circle cx="140" cy="267" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text16" x="140" y="269" class="control numeric-button button-text">⚙️</text>

            <!-- Rewind - Play\\Pause - Fast Forward -->
            <rect x="25" y="300" width="32.5" height="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text17" x="41" y="311" class="control numeric-button button-text force-font" style="font-size:12px">◀◀</text>
			    
            <rect x="70" y="300" width="45" height="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
			<text id="text18" x="92" y="311" class="control numeric-button button-text force-font" style="font-size:12px">▶\\❚❚</text>
			    
            <rect x="125" y="300" width="32.5" height="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text19" x="141" y="311" class="control numeric-button button-text force-font" style="font-size:12px;">▶▶</text>
                
            <!-- Custom Buttons - Group A -->
            <circle id="object51" cx="30" cy="80" r="12.5" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text51" class="control numeric-button button-text" x="30" y="82" style="fill:white; font-size:18px">A</text>

            <circle id="object52" cx="150" cy="80" r="12.5" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
            <text id="text52" class="control numeric-button button-text" x="150" y="82" style="fill:white; font-size:18px">B</text>

            <!-- Custom Buttons - Group B -->
            <circle id="object53" cx="35" cy="350" r="12" fill="red" stroke="gray" stroke-width="1"/>
            <text id="text53" class="control numeric-button button-text" x="35" y="351" >A</text>
            
            <circle id="object54" cx="75" cy="350" r="12" fill="green" stroke="gray" stroke-width="1"/>
            <text id="text54" class="control numeric-button button-text" x="75" y="351" >B</text>
            
            <circle id="object55" cx="110" cy="350" r="12" fill="purple" stroke="gray" stroke-width="1"/>
            <text id="text55" class="control numeric-button button-text" x="110" y="351" >C</text>
            
            <circle id="object56" cx="145" cy="350" r="12" fill="blue" stroke="gray" stroke-width="1"/>
            <text id="text56" class="control numeric-button button-text" x="145" y="351" >D</text>
            
            <!-- Custom buttons -->
            <circle id="object57" cx="35" cy="390" r="12" fill="red" stroke="gray" stroke-width="1"/>
            <text id="text57" class="control numeric-button button-text" x="35" y="391" >1</text>
            
            <circle id="object58" cx="75" cy="390" r="12" fill="green" stroke="gray" stroke-width="1"/>
            <text id="text58" class="control numeric-button button-text" x="75" y="391" >2</text>
            
            <circle id="object59" cx="110" cy="390" r="12" fill="purple" stroke="gray" stroke-width="1"/>
            <text id="text59" class="control numeric-button button-text" x="110" y="391" >3</text>
            
            <circle id="object60" cx="145" cy="390" r="12" fill="blue" stroke="gray" stroke-width="1"/>
            <text id="text60" class="control numeric-button button-text" x="145" y="391" >4</text>
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
                        }, 2000); /* Solid for 2 seconds */
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        led1.classList.remove('flicker'); /* Turn off LED1 */
                        led2.setAttribute('fill', 'red'); /* Set led2 to solid red */
                        setTimeout(() => {
                            led2.setAttribute('fill', '#555'); /* Reset led2 to its original color */
                        }, 2000); /* Solid for 2 seconds */
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
        def myCommandsList = []
        def supportedCommands = thisDevice.supportedCommands
        def hasOnOrOff = false
        def hasToggle = false
		def hasPush = false
		def hasDoubleTap = false
		
        supportedCommands.each { command ->
            def commandName = command.name
            myCommandsList << commandName
            if (commandName == 'on' || commandName == 'off') { hasOnOrOff = true  }
			if (commandName == 'toggle') { hasToggle = true  }
			if (commandName == 'push') { hasPush = true  }
			if (commandName == 'doubleTap') { hasDoubleTap = true  }
        }
        if (hasOnOrOff && !hasToggle) { myCommandsList << '*toggle' }
		if (hasPush) { myCommandsList.addAll(['*push1', '*push2', '*push3','*push4']) }
		if (hasDoubleTap) { myCommandsList.addAll(['*doubleTap1', '*doubleTap2', '*doubleTap3','*doubleTap4']) }
		
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
    if (state.initialized == true) {
        if (isLogTrace) log.trace("<b>initialize: Initialize has already been run. Exiting</b>")
        return
    }
    log.trace("<b>Running Initialize</b>")

	//Get the settings for the default profile.
	app.updateSetting("selectedProfile", 0)
	data = getProfile()
	
    //Initialze all the Fixed button settings
    for (int i = 1; i <= 19; i++) {
        app.updateSetting("myText$i", data.fixedButtonText[i - 1].toString() )
        app.updateSetting("myButtonColor$i", "#000000" )
        app.updateSetting("myTextColor$i", [value: "#FFFFFF", type: "color"])
        app.updateSetting("selectedButtonGroup", [value: "ONE", type: "enum"])
    }
	
	//Initialze all the custom button settings
    for (int i = 51; i <= 60; i++) {
        app.updateSetting("myText$i", data.customButtonText[i - 1].toString() )
        app.updateSetting("myButtonColor$i", "#000000" )
        app.updateSetting("myTextColor$i", [value: "#FFFFFF", type: "color"])
        app.updateSetting("selectedButtonGroup", [value: "ONE", type: "enum"])
    }
	

	
	//Remote Settings
	app.updateSetting("mySelectedRemote", "")
	app.updateSetting("myTitleText1", "Group 1" )
	app.updateSetting("unassignedButtonBehaviour", [value: "Normal", type: "enum"])

    //Publishing
	
    app.updateSetting("mySelectedRemote", "")
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


