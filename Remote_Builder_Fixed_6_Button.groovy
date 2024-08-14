/**
*  Remote Builder 6 Button
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
*  Remote Builder 6 Button - ChangeLog
*
*  Gary Milne - August 14th, 2024 @ 1:18 PM PM
*
*  Version 1.0.0 - Initial Public Release
*
* Possible Future Improvements:
* Loadable configurations such as lighting, audio, security, TV
* Default button group to have open on load.
*
**/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field

static def buttonGroup() { return ['ONE', 'TWO', 'THREE'] }

@Field static final codeDescription = "<b>Remote Builder - 6 Button 1.0 (8/14/24 @ 1:18 PM)</b>"
@Field static final codeVersion = 100
//@Field static final moduleName = "Custom 6 Button"
@Field static final moduleName = "Fixed 6 Button"

definition(
        //name: "Remote Builder - Custom 6 Button",
        //description: "Generates a Custom 6 Button remote control that can operate be executed from a web browser or embedded into a Hubitat Dashboard.",
        //importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Custom_6_Button.groovy",
	    name: "Remote Builder - Fixed 6 Button",
        description: "Generates a Fixed 6 Button remote control that can operate be executed from a web browser or embedded into a Hubitat Dashboard.",
        importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Fixed_6_Button.groovy",
        namespace: "garyjmilne", author: "Gary J. Milne", category: "Utilities", iconUrl: "", iconX2Url: "", iconX3Url: "", singleThreaded: true,
        parent: "garyjmilne:Remote Builder", 
        installOnOpen: true, oauth: true
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
			section(hideable: true, hidden: state.hidden.Intro, title: buttonLink('btnHideIntro', getSectionTitle("Introduction"), 20)) {
                text = "<b>Remote Builder</b> allows you to build compact remote control applets that you can customize to your liking. These applets provide a rich experience and allow distribution of control to any phone or tablet without the need for a full Dashboard. "
                text += "The applets are published via the url's in the <b>Endpoints</b> section and use Hubitat's security token mechanism. You can also publish remotes to the Hubitat dashboard just as you do with Tile Builder. "
				text += "The generated remote does not track state and is intended for use in the same way that a physical remote is used, when you are present in the environment that you are controlling."
                paragraph text
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
					if ( displayEndpoint == "Local" ) paragraph '<iframe src="' + state.localEndpoint + '" width="140" height="240" style="border:solid" scrolling="no"></iframe>'
					if (displayEndpoint == "Cloud" ) paragraph '<iframe src="' + state.cloudEndpoint + '" width="140" height="240" style="border:solid" scrolling="no"></iframe>'
				}
				
                input(name: "Compile", type: "button", title: "Compile Changes", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 12)
				text = "<b>Important:</b> This is a live remote. Pressing any of the buttons will execute the actions you have assigned to the buttons below.<br>"
				text += "When you make changes to properties of the remote they do not take effect until they have been Compiled.<br>"
				paragraph text
				
				text = "The <b>LED on the upper left</b> will flash orange when a button has been pushed and data is being sent.<br>"
				text += "The <b>icon on the top center</b> will either be a house or a cloud indicating which endpoint you are connected to.<br>"
				text += "The <b>LED on the upper right</b> will go to steady green when data is received. It will go to steady red if an transmission error is detected.<br>"
				text += "The <b>6 round buttons</b> 1-6, 7-12 and 13-18 are all programmable in terms of function and decoration. A single click of the button will cause the programmed action to be taken. There is no concept of double-click or long-click.<br>"
				text += "The <b>rectangular button on the bottom left (| || |||)</b> toggles between the three available button groups.<br>"
				text += "The <b>rectangular button on the bottom right (?)</b> toggles 'Help Mode' on or off. When in 'Help Mode' pressing the action buttons will reveal the tooltip. This is intended for use on touch devices where there is no concept of 'mouse-over'.<br>"
				text += "The <b>label on the bottom</b> of the remote in slightly faded white text is user configurable and can be used as a reminder for the purpose of a particular button group. You can aslo paste eMoji's in here if preferred."
				paragraph summary ("Explanation of Remote Layout", text)
            }
        
			section(hideable: true, hidden: state.hidden.Customize, title: buttonLink('btnHideCustomize', getSectionTitle("Customize"), 20)) {
				def startIndex, endIndex
                if (moduleName == "Custom 6 Button")  input(name: "selectedButtonGroup", type: "enum", title: bold("Select Button Group to Customize"), options: buttonGroup(), required: true, defaultValue: "ONE", submitOnChange: true, width: 3)
				if(selectedButtonGroup == "ONE") { 
					startIndex = 1; endIndex = 6; 
					input ("myRemoteBackground1", "color", title: "&nbsp<b>Group 1 Background Color</b>", required: false, defaultValue: "#555", width: 3, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")  
					input ("myTitleText1", "text", title: "&nbsp<b>Group 1 Title Text</b>", required: false, defaultValue: "#000", width: 3, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px", newLineAfter:true)  
					}
				if(selectedButtonGroup == "TWO") { 
					startIndex = 7; endIndex = 12;  
					input ("myRemoteBackground2", "color", title: "&nbsp<b>Group 2 Background Color</b>", required: false, defaultValue: "#833", width: 3, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")  
					input ("myTitleText2", "text", title: "&nbsp<b>Group 2 Title Text</b>", required: false, defaultValue: "#000", width: 3, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px", newLineAfter:true)  
					}
				if(selectedButtonGroup == "THREE") { 
					startIndex = 13; endIndex = 18; 
					input ("myRemoteBackground3", "color", title: "&nbsp<b>Group 3 Background Color</b>", required: false, defaultValue: "#7AD", width: 3, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
					input ("myTitleText3", "text", title: "&nbsp<b>Group 3 Title Text</b>", required: false, defaultValue: "#000", width: 3, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px", newLineAfter:true)  
					}
                paragraph line(1)
                
                (startIndex..endIndex).each { i ->
                    input ("myDevice$i", "capability.*", title: "<b>Button $i Device</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px") /* top right bottom left */
                    input ("myCommand$i", "enum", title: "&nbsp<b>Command</b>", options: getCommandList(settings["myDevice$i"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					if (moduleName == "Custom 6 Button") { 
                    	input ("myButtonColor$i", "color", title: "&nbsp<b>Button Color</b>", required: false, width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
                    	//input ("myText$i", "text", title: "&nbsp<b>Character</b>", multiple: false, submitOnChange: true, width: 1, required: true, defaultValue: getDefaultButtonText(i), style: "margin: 2px 10px 2px 10px; padding:3px;border: 1px solid gray")
						input ("myText$i", "text", title: "&nbsp<b>Character</b>", multiple: false, submitOnChange: true, width: 1, required: true, style: "margin: 2px 10px 2px 10px; padding:3px;border: 1px solid gray")
                    	input ("myTextColor$i", "color", title: bold("&nbsp<b>Text Color</b>"), required: false, defaultValue: "#FFFFFF", width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
					}
                    input ("myTooltip$i", "text", title: "&nbsp<b>Tooltip (optional)</b>", multiple: false, submitOnChange: true, width: 2, required: false, defaultValue: "?", style: "margin: 2px 10px 2px 10px; padding:3px; border: 1px solid gray;")
                    paragraph line(1)
                } 
				input(name: "disableUnassignedButtons", type: "enum", title: bold("Disable Unassigned Buttons"), options: ["True", "False"], required: false, defaultValue: "Enabled", submitOnChange: true, width: 3, style:"margin-right: 20px")
            }
        
        //Start of Publish Section
		section(hideable: true, hidden: state.hidden.Publish, title: buttonLink('btnHidePublish', getSectionTitle("Publish"), 20)) {
            input(name: "myRemote", title: "<b>Attribute to store the Remote?</b>", type: "enum", options: parent.allTileList(), required: true, submitOnChange: true, width: 2, defaultValue: 0, newLine: false)
            input(name: "myRemoteName", type: "text", title: "<b>Name this Remote</b>", submitOnChange: true, width: 2, newLine: false, required: true)
            input(name: "tilesAlreadyInUse", type: "enum", title: bold("For Reference Only: Remotes in Use"), options: parent.getTileList(), required: false, defaultValue: "Remotes List", submitOnChange: true, width: 2)
                                    
            if (myRemoteName) app.updateLabel(myRemoteName)
            myText =  "The <b>Remote Name</b> given here will also be used as the name for this instance of Remote Builder. Appending the name with your chosen tile number can make parent display more readable.<br>"
            myText += "Only links to the Local and Cloud Endpoints are stored in the Remote Builder Storage Device. From there they can be published on the Dashboard if desired.<br>"
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
				paragraph "In this section you can enable logging of any connection and action requests received.<br>You can also rebuild the endpoints if you choose to refresh the OAuth client secret"				
                input(name: "isLogDebug", type: "bool", title: "<b>Enable Debug logging?</b>", defaultValue: false, submitOnChange: true, width: 3, newLine: true)
                input(name: "isLogErrors", type: "bool", title: "<b>Log errors encountered?</b>", defaultValue: true, submitOnChange: true, width: 3)
				input(name: "isLogConnections", type: "bool", title: "<b>Record All Connection Requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogActions", type: "bool", title: "<b>Record All Action Requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
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
    
    // Loop from 1 to 18 and create data for each group
    (1..18).each { i ->
        def index = (i)
		if (  ( settings."myDevice$i" == null || settings."myCommand$i" == null )  && disableUnassignedButtons == "True" ){
			data = [ "index": "$index", "label": "?", "tooltip": "?", "bColor": "#555", "tColor": "#555"]
		}
		else data = [ "index": "$index", "label": settings."myText$i", "tooltip": settings."myTooltip$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" ]
        jsonGroup << data
    }
	
	// Convert the list of maps to a JSON string and save them to state
    state.buttonData = JsonOutput.toJson(jsonGroup)
	state.backgroundData = '["' + settings."myRemoteBackground1" + '","' + settings."myRemoteBackground2" + '","' + settings."myRemoteBackground3" + '"]'
	state.titlesData = '["' + settings."myTitleText1" + '","' + settings."myTitleText2" + '","' + settings."myTitleText3" + '"]'
	
	if (isLogDebug) log.debug ("ButtonData is: $state.buttonData")
	if (isLogDebug) log.debug ("backgroundData is: $state.backgroundData")
	if (isLogDebug) log.debug ("titlesData is: $state.titlesData")

    def String content = myHTML()
    content = content.replace("#buttonData#", state.buttonData )
	content = content.replace("#backgroundData#", state.backgroundData )
	content = content.replace("#titlesData#", state.titlesData )
	
	//When we are in Fixed 6 Button mode then we disable the Mode Button because buttons 7-18 are deaactivated.
	if (moduleName == "Fixed 6 Button") content = content.replace("#disableModeButton#", "no-cursor" )
	if (moduleName == "Custom 6 Button") content = content.replace("#disableModeButton#", "" )
	
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
    def i = (button.replace("button","")).toInteger()
                        
	myDevice = settings["myDevice$i"]
    myCommand = settings["myCommand$i"]
	
	// Record the action request
    if (isLogActions) log.info ( "Remote Builder Data Received - Remote: $myRemote - Name: $myRemoteName - Button: $i - Device: $myDevice - Command: $myCommand")
	
	if (myDevice == null || myCommand == null) return
	
    def myCommandIndex = 0
    if (myCommand && myCommand[-1] in '1'..'4') {
        myCommandIndex = myCommand[-1] as int
    }
    
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
            if (myDevice != null && myCommand != null ) myDevice."${myCommand}"()
			}
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
        html, body { align-items:center; display:flex; font-family:Arial; sans-serif; font-size:20px; justify-content:center; padding:0; user-select:none; height:100vh; background-color:transparent; }
        .container { background-color:transparent; padding:0px; border-radius:4px;position:relative; height:80%; overflow:hidden}
        .control { cursor:pointer }
        .no-cursor { font-size:100%; font-weight:bold; pointer-events:none; text-anchor:middle; }
        .hidden { display:none }
        .tooltip { background-color:yellow; border-radius:4px; color:black; display:none; left:50%; font-size:70%; padding:5px; pointer-events:none; position:absolute; text-align:center; top:5%; transform: translateX(-50%); width:80%; }
		.mode-button { font-size:14px; letter-spacing:3px;}

		/* Animation */
		.flicker {animation:flickerOrange 0.25s linear forwards}  
		@keyframes flickerOrange {0%, 20%, 40%, 60%, 80%, 100% {fill:#555} 10%, 30%, 50%, 70%, 90% {fill:orange}}
    </style>
</head>
<body>
	<div class="container" style="display:flex; justify-content:center; align-items:center; height:100vh; overflow:hidden;">
    	<svg id="remote" viewBox="0 0 100 200" preserveAspectRatio="xMidYMid meet" style="width:100%; height:90%">
 	   		<defs>
               <linearGradient id="shadow" x1="0%" y1="0%" x2="0%" y2="100%">
                    <stop offset="0%" stop-color="white" stop-opacity="0.2" />
                    <stop offset="100%" stop-color="black" stop-opacity="0.7" />
                </linearGradient>
            </defs>

            <!-- Remote Body -->
            <rect id="remote-body" x="0" y="0" width="100%" height="100%" rx="10" ry="10"/>

            <!-- LED -->
            <circle id="led1" cx="15%" cy="7%" r="5%" fill="#555"/>
            <circle id="led2" cx="85%" cy="7%" r="5%" fill="#555"/>

            <!-- Local\\Cloud Indicator -->
			<text class="no-cursor" id="connection-text" x="50%" y="10%" text-anchor="middle" fill="#fff" font-weight="bold" style="font-size:17px">#connectionIcon#</text>

            <!-- Buttons Container -->
            <g>
                <!-- Mode 1 Buttons -->
                <circle class="control numeric-button" id="button1" cx="25%" cy="25%" r="10%" stroke="url(#shadow)" stroke-width="3" />
                <text id="text1" class="no-cursor" x="25%" y="25.5%" dy="3%">1</text> 

                <circle class="control numeric-button" id="button2" cx="75%" cy="25%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text2" class="no-cursor" x="75%" y="25.5%" dy="3%">2</text>

                <circle class="control numeric-button" id="button3" cx="25%" cy="45%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text3" class="no-cursor" x="25%" y="45.5%" dy="3%">3</text>

                <circle class="control numeric-button" id="button4" cx="75%" cy="45%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text4" class="no-cursor" x="75%" y="45.5%" dy="3%">4</text>

                <circle class="control numeric-button" id="button5" cx="25%" cy="65%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text5" class="no-cursor" x="25%" y="65.5%" dy="3%">5</text>

                <circle class="control numeric-button" id="button6" cx="75%" cy="65%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text6" class="no-cursor" x="75%" y="65.5%" dy="3%">6</text>

                <!-- Mode 2 Buttons -->
                <circle class="control numeric-button hidden" id="button7" cx="25%" cy="25%" r="10%" stroke="url(#shadow)" stroke-width="3" />
                <text id="text7" class="no-cursor hidden" x="25%" y="25.5%" dy="3%">A</text> 

                <circle class="control numeric-button hidden" id="button8" cx="75%" cy="25%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text8" class="no-cursor hidden" x="75%" y="25.5%" dy="3%">B</text>

                <circle class="control numeric-button hidden" id="button9" cx="25%" cy="45%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text9" class="no-cursor hidden" x="25%" y="45.5%" dy="3%">C</text>

                <circle class="control numeric-button hidden" id="button10" cx="75%" cy="45%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text10" class="no-cursor hidden" x="75%" y="45.5%" dy="3%">D</text>

                <circle class="control numeric-button hidden" id="button11" cx="25%" cy="65%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text11" class="no-cursor hidden" x="25%" y="65.5%" dy="3%">E</text>

                <circle class="control numeric-button hidden" id="button12" cx="75%" cy="65%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text12" class="no-cursor hidden" x="75%" y="65.5%" dy="3%">F</text>

                <!-- Mode 3 Buttons -->
                <circle class="control numeric-button hidden" id="button13" cx="25%" cy="25%" r="10%" stroke="url(#shadow)" stroke-width="3" />
                <text id="text13" class="no-cursor hidden" x="25%" y="25.5%" dy="3%">➕</text> 

                <circle class="control numeric-button hidden" id="button14" cx="75%" cy="25%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text14" class="no-cursor hidden" x="75%" y="25.5%" dy="3%">➖</text>

                <circle class="control numeric-button hidden" id="button15" cx="25%" cy="45%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text15" class="no-cursor hidden" x="25%" y="45.5%" dy="3%">💡</text>

                <circle class="control numeric-button hidden" id="button16" cx="75%" cy="45%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text16" class="no-cursor hidden" x="75%" y="45.5%" dy="3%">🔌</text>

                <circle class="control numeric-button hidden" id="button17" cx="25%" cy="65%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text17" class="no-cursor hidden" x="25%" y="65.5%" dy="3%">X</text>

                <circle class="control numeric-button hidden" id="button18" cx="75%" cy="65%" r="10%" stroke="url(#shadow)" stroke-width="3"/>
                <text id="text18" class="no-cursor hidden" x="75%" y="65.5%" dy="3%">Y</text>
            </g>

            <!-- Mode Button -->
            <rect id="mode-button" class="control #disableModeButton#" x="10%" y="78%" width=30% height=10% fill="#666" stroke="white" stroke-width="1" rx="2%" ry="2%"/>            
			<text class="no-cursor mode-button #disableModeButton#" id="mode-text" x="26%" y="84.5%" text-anchor="middle" fill="#fff" font-weight="bold"></text>

			<rect id="help-button" class="control" x="60%" y="78%" width=30% height=10% fill="#666" stroke="white" stroke-width="1" rx="2%" ry="2%"/>
			<text class="no-cursor mode-button" id="help-button-text" x="76%" y="86%" text-anchor="middle" fill="#fff" style="font-size:18px" font-weight="bold">?</text>

			<text class="no-cursor" id="title-text" x="50%" y="96%" text-anchor="middle" fill="#fff" style="font-size:12px; opacity:0.5 ">?</text>
			
        </svg>
        <div id="tooltip" class="tooltip"></div>
    </div>

<script>
    document.addEventListener('DOMContentLoaded', () => {
		const modeButton = document.getElementById('mode-button');
		const modeText = document.getElementById('mode-text');
		const titleText = document.getElementById('title-text');
		const helpButton = document.getElementById('help-button');
		const helpButtonText = document.getElementById('help-button-text');
		const numericButtons = document.querySelectorAll('.numeric-button');
		const tooltip = document.getElementById('tooltip');
		const led1 = document.getElementById('led1');
		const led2 = document.getElementById('led2');
		const modes = ['|', '||', '|||'];		
		const remoteBackground = document.getElementById('remote-body');
		let helpMode = false;  /* Track help mode state */
		
		const buttonData = #buttonData#;
		const backgroundData = #backgroundData#;
		const titlesData = #titlesData#;

        /* Get the saved mode index from localStorage or default to 0 */
		let modeIndex = localStorage.getItem('modeIndex') ? parseInt(localStorage.getItem('modeIndex')) : 0;
		modeIndex = 0
				
		console.log("modeIndex: ", modeIndex );
        modeText.textContent = modes[modeIndex];
		titleText.textContent = titlesData[modeIndex];

        modeButton.addEventListener('click', () => {
            modeIndex = (modeIndex + 1) % modes.length;
            localStorage.setItem('modeIndex', modeIndex);
            modeText.textContent = modes[modeIndex];
            updateButtons();
			updateBackground();
			updateTitle();
        });

		helpButton.addEventListener('click', () => {
            helpMode = !helpMode;
			/* console.log("Helpmode is: ", helpMode ); */
            helpButtonText.setAttribute('fill', helpMode ? 'green' : '#fff');
        });

	numericButtons.forEach(button => {
    	button.addEventListener('click', (event) => {
        	const buttonId = event.target.id; /* Get the ID of the clicked button */
        	if (helpMode) {
            	const buttonInfo = buttonData.find(b => `button${b.index}` === buttonId);
            	if (buttonInfo) { 
                	showTooltip(event, buttonInfo.tooltip);
            	}
        	} 
			else { sendData(buttonId); }
    });

    button.addEventListener('mouseover', (event) => {
        if (!helpMode) {
            const buttonId = event.target.id;
            const buttonInfo = buttonData.find(b => `button${b.index}` === buttonId);
            if (buttonInfo) { 
                showTooltip(event, buttonInfo.tooltip);
            }
        }
    });

    button.addEventListener('mouseout', () => { hideTooltip(); });
});

/*  Start of <SCRIPT> functions */
function updateButtons() {
    for (let i = 1; i <= 18; i++) {
        const buttonElement = document.getElementById(`button${i}`);
        const textElement = document.getElementById(`text${i}`);
		const tooltipElement = document.getElementById(`tooltip${i}`);
        if (buttonElement && textElement) {
            /* Hide all buttons initially */
            buttonElement.classList.add('hidden');
            textElement.classList.add('hidden');
            
            /* Show buttons for the current mode */
            const button = buttonData.find(b => b.index == i);
            if ((modeIndex === 0 && i <= 6) || (modeIndex === 1 && i > 6 && i <= 12) || (modeIndex === 2 && i > 12)) {
                if (button) {
                    buttonElement.classList.remove('hidden');
                    textElement.classList.remove('hidden');
                    buttonElement.setAttribute('fill',button.bColor);
                    textElement.textContent = button.label;
                    textElement.setAttribute('fill',button.tColor);
                }
            }
        }
    }
};

function updateBackground() { remoteBackground.setAttribute('fill', backgroundData[modeIndex]); }

function updateTitle() { titleText.textContent = titlesData[modeIndex]; }

function showTooltip(event, tooltipText) {
	if (tooltipText && tooltipText !== "?") {
    	tooltip.textContent = tooltipText;
        tooltip.style.display = 'block';
	} else {tooltip.style.display = 'none'}
};

function hideTooltip() {tooltip.style.display='none'};

function sendData(buttonId) {
	/* console.log("Helpmode2 is: ", helpMode ); */
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
            }, 2000); /* Solid for 2 second */
        });
    }
    setTimeout(() => {
        led1.classList.remove('flicker');
    }, 250);
}

/* Initialize buttons */
updateButtons();
updateBackground();
updateTitle();

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
		case "Test":
			test()
			break
        case "publishRemote":
            publishRemote()
            break
		case 'btnHideIntro':
            state.hidden.Intro = state.hidden.Intro ? false : true
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
       
    //Initialze all the default settings
    def buttonTextList = ["1", "2", "3", "4", "5", "6", "A", "B", "C", "D", "E", "F", "▶️", "⏹️", "⬅️", "➡️", "➕", "➖"]
    def buttonColorList = ["#FF0000", "#FFA500", "#0000FF", "#008000", "#00FFFF", "#FF00FF", \
                           "#FF0000", "#FFA500", "#0000FF", "#008000", "#00FFFF", "#FF00FF", \
                           "#00A6ED", "#00A6ED", "#00A6ED", "#00A6ED", "#00A6ED", "#00A6ED"]
    
    for (int i = 1; i <= 18; i++) {
        app.updateSetting("myText$i", buttonTextList[i - 1] )
        app.updateSetting("myTooltip$i", "?" )
        app.updateSetting("myButtonColor$i", buttonColorList[ i - 1 ] )
        app.updateSetting("myTextColor$i", [value: "#FFFFFF", type: "color"])
        app.updateSetting("selectedButtonGroup", [value: "ONE", type: "enum"])
    }
	
	//Remote Settings
	app.updateSetting("myRemoteBackground1", [value: "#A05050", type: "color"])
	app.updateSetting("myRemoteBackground2", [value: "#506090", type: "color"])
	app.updateSetting("myRemoteBackground3", [value: "#509090", type: "color"])
	app.updateSetting("myTitleText1", "Group 1" )
	app.updateSetting("myTitleText2", "Group 2" )
	app.updateSetting("myTitleText3", "Group 3" )
	app.updateSetting("disableUnassignedButtons", [value: "False", type: "enum"])

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
    state.hidden = [Intro: false, Endpoints: true, Display: false, Customize: true, Publish: true]
    
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

def test(){
	//input(name: "testIcons", type: "enum", title: bold("Disable Unassigned Buttons"), options: test(), required: false, submitOnChange: true, width: 3, style:"margin-right: 20px")
	def myIconList = []
    def iconNames = [
    'face', 'home', 'menu', 'search', 'settings', // Add more icon names as needed
    'account_circle', 'alarm', 'build', 'delete', 'favorite',
    'info', 'lock', 'message', 'notifications', 'shopping_cart' ]

	iconNames.each { icon -> log.info "<span class=material-icons>${icon}</span>"  
	myIconList << "<span class=material-icons>${icon}</span>" }
	return myIconList.unique().sort()
}

