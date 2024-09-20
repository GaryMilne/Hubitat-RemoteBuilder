/**
*  Remote Builder Keypad
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
*  Remote Builder Keypad - ChangeLog
*
*  Gary Milne - Sept 20th, 2024 @ 8:40 AM
*
*  Version 1.0.0 - Internal Use Only
*  Version 2.0.0 - First Public Release. Uses HTML\CSS only.
*
**/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field

@Field static final codeDescription = "<b>Remote Builder - Keypad 2.0.0 (9/20/24)</b>"
@Field static final codeVersion = 200
@Field static final moduleName = "Keypad"

definition(
        name: "Remote Builder - Keypad",
        description: "Generates a Keypad that can be executed from a web browser or embedded into a Hubitat Dashboard.",
        importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Keypad.groovy",
        namespace: "garyjmilne", author: "Gary J. Milne", category: "Utilities", iconUrl: "", iconX2Url: "", iconX3Url: "", singleThreaded: true,
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
				input(name: "myTitle", type: "text", title: "<b>Title or blank for none</b>", submitOnChange: true, width: 2, newLine: false, required: false)
                
				if ( state.compiledLocal == null || state.compiledCloud == null ) paragraph ("<span style=color:red><b>Press Compile Changes to Generate Remote</b></span>" )
				else {
					if ( displayEndpoint == "Local" ) paragraph '<iframe src="' + state.localEndpoint + '" width="200" height="300" style="border:solid" scrolling="no"></iframe>'
					if (displayEndpoint == "Cloud" ) paragraph '<iframe src="' + state.cloudEndpoint + '" width="200" height="300" style="border:solid" scrolling="no"></iframe>'
				}
				
                input(name: "Compile", type: "button", title: "Compile Changes", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 12)
				text = "<b>Important:</b> This is a live remote and will execute the actions you have assigned when the appropriate buttons are pressed.<br>"
				paragraph text
				
				text = "The <b>LED on the upper left</b> will flash orange when a button has been pushed and data is being sent.<br>"
				text += "The <b>icon on the top center</b> will either be a house or a cloud indicating which endpoint you are connected to.<br>"
				text += "The <b>LED on the upper right</b> will go to steady green when data is received. It will go to steady red if an transmission error is detected.<br>"
				text += "The <b>🗑️</b> button will clear the contents of the text field.<br>"
				text += "The <b>⏎</b> button will send the contents of the text field to the Hub.<br>"
				paragraph summary ("Explanation of Remote Layout", text)
            }
		
		   section(hideable: true, hidden: state.hidden.InputHandling, title: buttonLink('btnHideInputHandling', getSectionTitle("InputHandling"), 20)) {
               input(name: "sendDataTo", type: "enum", title: bold("Send Data To:"), options: ["Device", "Hub Variable", "Compare"], required: false, defaultValue: "Device", submitOnChange: true, width: 2, style:"margin-right: 20px")
			   
			   if (sendDataTo == "Device" ) {
				   input ("myDevice", "capability.*", title: "<b>Device</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px")
				   input ("myCommand", "enum", title: "&nbsp<b>Command</b>", options: getCommandList(settings["myDevice"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
				   paragraph "<b>Note:</b> The received code will be sent to the specified device\\command as the first parameter."
			   }
			   if (sendDataTo == "Hub Variable" ) {
				   input "myHubVariable", "enum", title: "<b>Hub String Variable</b>", submitOnChange: true, width: 2, options: getAllGlobalVars().findAll { it.value.type == "string" }.keySet().collect().sort { it.capitalize() } 
				   paragraph "<b>Note:</b> The received code will be sent to the specified Hub variable. You can use this method to trigger a rule by monitoring a Hub variable."   
			   }
				if (sendDataTo == "Compare" ) {
					paragraph line(1)
					input(name: "myCompare1", type: "text", title: "<b>Compare 1</b>", submitOnChange: true, width: 2, newLine: false, required: false)
					input ("myDevice1", "capability.*", title: "<b>Device 1</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px")
				    input ("myCommand1", "enum", title: "&nbsp<b>Command 1</b>", options: getCommandList(settings["myDevice1"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					paragraph line(1)
					input(name: "myCompare2", type: "text", title: "<b>Compare 2</b>", submitOnChange: true, width: 2, newLine: false, required: false)
					input ("myDevice2", "capability.*", title: "<b>Device 2</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px")
				    input ("myCommand2", "enum", title: "&nbsp<b>Command 2</b>", options: getCommandList(settings["myDevice2"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					paragraph line(1)
					input(name: "myCompare3", type: "text", title: "<b>Compare 3</b>", submitOnChange: true, width: 2, newLine: false, required: false)
					input ("myDevice3", "capability.*", title: "<b>Device 3</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px")
				    input ("myCommand3", "enum", title: "&nbsp<b>Command 3</b>", options: getCommandList(settings["myDevice3"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					paragraph line(1)
					input(name: "myCompare4", type: "text", title: "<b>Compare 4</b>", submitOnChange: true, width: 2, newLine: false, required: false)
					input ("myDevice4", "capability.*", title: "<b>Device 4</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px")
				    input ("myCommand4", "enum", title: "&nbsp<b>Command 4</b>", options: getCommandList(settings["myDevice4"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					paragraph line(1)
					input(name: "myCompare5", type: "text", title: "<b>Compare 5</b>", submitOnChange: true, width: 2, newLine: false, required: false)
					input ("myDevice5", "capability.*", title: "<b>Device 5</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin-right: 50px; padding:3px")
				    input ("myCommand5", "enum", title: "&nbsp<b>Command 5</b>", options: getCommandList(settings["myDevice5"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					paragraph line(2)
					paragraph "<b>Note:</b> If the received code matches a comparison value the specified device\\command will be executed. The received code will not be passed as a parameter."   
			   }
        }
		
        //Start of Publish Section
		section(hideable: true, hidden: state.hidden.Publish, title: buttonLink('btnHidePublish', getSectionTitle("Publish"), 20)) {
            input(name: "myRemote", title: "<b>Attribute to store the Remote?</b>", type: "enum", options: parent.allTileList(), required: true, submitOnChange: true, width: 2, defaultValue: 0, newLine: false)
            input(name: "myRemoteName", type: "text", title: "<b>Name this Remote</b>", submitOnChange: true, width: 2, newLine: false, required: true)
            input(name: "tilesAlreadyInUse", type: "enum", title: bold("For Reference Only: Remotes in Use"), options: parent.getTileList(), required: false, defaultValue: "Remotes List", submitOnChange: true, width: 3)
			                        
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
    
	def String content = myHTML()
	def title 
	
	// Strip all the comments out of the file to save space.
    content = condense(content)
	
	if (myTitle == "?" || myTitle == null || myTitle == "" ) title = " "
	else title = myTitle
        
    // Create separate copies of content for local and cloud versions
    def localContent = content
	localContent = localContent.replace('#connectionIcon#', '⌂' )
    localContent = localContent.replace("#URL#", state.localEndpoint )
	localContent = localContent.replace("#Title#", title )
    
	def cloudContent = content
	cloudContent = cloudContent.replace('#connectionIcon#', '☁︎' )
    cloudContent = cloudContent.replace("#URL#", state.cloudEndpoint )
	cloudContent = cloudContent.replace("#Title#", title )
		
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
    def myCode = parsedBody.code
    
	if (sendDataTo.toString() == "Device" ) {
		if (myDevice == null || myCommand == null || myCode == null ) {
			if (isLogActions) log.info ("Received Code: Event not processed because a null value was found. Device: $myDevice  Command: $myCommand  Code: $myCode ")
			return
			}
		else {
			if (isLogActions) log.info ("Received Code: Executing command $myCommand on device $myDevice with parameter: $myCode")
			myDevice."${myCommand}"(myCode)
			return
			}		
	}
	
	if (sendDataTo.toString() == "Hub Variable" ) {
		if (isLogActions) log.info ("Received Code: Setting Hub Variable $myHubVariable to value: $myCode")
		setGlobalVar(myHubVariable, myCode)
		addInUseGlobalVar(myHubVariable)
	}
	
	if (sendDataTo.toString() == "Compare" ) {
		def foundMatch = false
		if (isLogActions) log.info ("Received Code: Performing Compare")
		
		// Loop from 1 to 5
		(1..5).each { i ->
			def thisCompare = settings."myCompare$i"
			def thisDevice = settings."myDevice$i"
			def thisCommand = settings."myCommand$i"
			if (isLogDebug) log.info ("myCode: $myCode -- myCompare: $thisCompare")
    		if (myCode == thisCompare) {
				foundMatch = true
        		executeDeviceCommand(thisDevice, thisCommand)
			}
		}
		if (isLogActions && foundMatch == false) log.info ("No matches found. Nothing done.")
	}
	
}

//Executes commands when a comparison value is matched.
def executeDeviceCommand( device, command ){
	if (isLogDebug) log.info ("Received: Device:$device and Command:$command ")
	if (device == null || command == null ) {
			if (isLogActions) log.info ( "Received Code: Event not processed because a null value was found. Device: $myDevice  Command: $myCommand  Code: $myCode ")
			return
			}
		else {
			if (isLogActions) log.info ("Execute Command: $command for device $device")
			device."${command}"()
			return
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
    <title>Keypad</title>
	<link rel="icon" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAACXBIWXMAAAsTAAALEwEAmpwYAAABuElEQVR4nO1ZzWrCQBDOe7THtg+UQ3JSd2KOikFKn0aPCkU8GXOK6K19gvaYPEAD1QfolEmV5qfSbOomS5kPPhjWYXe+nWEmMYbBYDD+L1zXvRZCLIQQBwDAJim+zlwKIW7/EnzSdOBQFpJ0u90raQF0820HD98iHusIOGgk4F1awLnN1us1RlGEcRynJJvWsj5BEJR8fN+X3gcyvJiA7KHZw1X4gAoBxUNPVOEDnAEo38R2uy2lfbVa5Xyo3n+r7yr7gIoMOI6DnufheDxOORqNanUW2X2MSwloi4aqDJBNayp8QIWA3W5X6hxUzyp8gLsQ8BxA7Sax/0OPrzIH6vgAt1Fov/8DzwHgOYBadaGY3weA50AltN02gQcZnL+NwWCAw+EwJdnF3yu9DwDgfa+HD0eS7TSRgfl8jpvNJsfZbCb9rP9qWYimmeOLZakXEIZhSQCtyfb4j0LwaJrpmnIBxeBPlJ0VxeDxSC0EaJ2B6XSaKyOyJ5OJ9LP+s23nRJD9ZNsXFbBX0c+b/Hd6qZGAhbSATqdzBwBvGgSf9Pv9G6MO6NMOfR1pqZz2dPO1g2cwGAyjCXwCmF7m5Awv6SgAAAAASUVORK5CYII=" type="image/png">
	<!-- Include Material Symbols Font -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />
	
    <style>
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
			aspect-ratio: 1 / 1.9; /* This makes the height equal to the width, creating a square */
			background:linear-gradient(#444, #222);
			border: .25vh solid #222;
			border-radius: .5vh;
			box-shadow:0px 0px 1vh 1vh #333;
			position: relative;
			outline:0.25vh solid white;
		}	
		
		.button {
			aspect-ratio: 1 / 1.25; /* This makes the height equal to the width, creating a square */
			cursor: pointer;
			color:white;
			display: flex;
            fill: #555;
			height:15%;
			stroke: #000;
            stroke-width: 1;
			position: absolute;
			}

		/* Standard visible button */
		.button-normal{
			background-clip: padding-box, border-box; /* Clip background to show gradient on border */
			background-image: linear-gradient(to bottom, rgba(128, 128, 128, 0.7) 0%, rgba(30, 30, 30, 0.8) 50%, rgba(128, 128, 128, 0.7) 100%),
							  linear-gradient(to bottom, rgba(0, 0, 0, 0.7) 0%, rgba(255, 255, 255, 1) 50%, rgba(0, 0, 0, 0.3) 100%); /* Gradient for border */
			background-origin: border-box;
			border: 0.2vh solid transparent; /* Border will be transparent initially */
		}

		#erase-btn:hover {
            background-color: #AA6666 !important;
        }

        #submit-btn:hover {
            background-color: #0056b3 !important;
        }
				
		/* Start of general text formatting classes */
		.dynamic-font-2 { font-size: clamp(4px, 2vh, 20px) !important; }
		.dynamic-font-3 { font-size: clamp(6px, 3vh, 30px) !important; }
		.dynamic-font-4 { font-size: clamp(8px, 4vh, 40px) !important; }
		.dynamic-font-5 { font-size: clamp(9px, 5vh, 50px) !important; }
		.dynamic-font-6 { font-size: clamp(10px, 6vh, 60px) !important;}	

		/* Standard Text Elelement */
		.text-element {
            position: absolute;
            font-size: 3vh;
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
        <div class="text-element dynamic-font-3" style="left:50%; top:1%; color:#888" >#connectionIcon#</div>
		
		<div id="led1" class="text-element center" style="left:5%; top:3%">⬤</div>
		<div id="led2" class="text-element center" style="left:95%; top:3%">⬤</div>
		
		<!-- Customize the Power Button to make it round rather than rectangular -->
        <button id="text1" class="myButtons button button-normal center dynamic-font-6" data-number="1" style="left:20%; top:15%" >1</button>
		<button id="text2" class="myButtons button button-normal center dynamic-font-6" data-number="2" style="left:50%; top:15%" >2</button>
		<button id="text3" class="myButtons button button-normal center dynamic-font-6" data-number="3" style="left:80%; top:15%" >3</button>
		
		<button id="text4" class="myButtons button button-normal center dynamic-font-6" data-number="4" style="left:20%; top:32.5%" >4</button>
		<button id="text5" class="myButtons button button-normal center dynamic-font-6" data-number="5" style="left:50%; top:32.5%" >5</button>
		<button id="text6" class="myButtons button button-normal center dynamic-font-6" data-number="6" style="left:80%; top:32.5%" >6</button>
		
		<button id="text7" class="myButtons button button-normal center dynamic-font-6" data-number="7" style="left:20%; top:50%" >7</button>
		<button id="text8" class="myButtons button button-normal center dynamic-font-6" data-number="8" style="left:50%; top:50%" >8</button>
		<button id="text9" class="myButtons button button-normal center dynamic-font-6" data-number="9" style="left:80%; top:50%" >9</button>
		
		<button id="text10" class="myButtons button button-normal center dynamic-font-6" data-number="*" style="left:20%; top:67.5%" >✱</button>
		<button id="text11" class="myButtons button button-normal center dynamic-font-6" data-number="0" style="left:50%; top:67.5%" >0</button>
		<button id="text12" class="myButtons button button-normal center dynamic-font-6" data-number="#" style="left:80%; top:67.5%" >#</button>
		
		<button id="erase-btn" class="button center dynamic-font-4" style="left:16%; top:84%; height:10vh; width:10vh; background-color:#FF6666; padding:0px" >🗑️</button>
		<button id="submit-btn" class="button center dynamic-font-5" style="left:84%; top:84%; height:10vh; width:10vh; background-color:#007BFF; padding:0px" >⏎</button>
		<input type="text" id="text-field" class="text-field center dynamic-font-5" readonly style="left:50%; top:84%; height:9vh; width:35%; padding:0px; background-color: white; color:black; position: absolute; margin-right: 1vh; border: 1px solid #F00;" />		
		<div class="text-element center dynamic-font-4" style="left:50%; top:95%; color:#888; width:90%" >#Title#</div>				    
    </div>		

	<script>
        const myButtons = document.querySelectorAll('.myButtons');
		const eraseBtn = document.getElementById('erase-btn');
        const submitBtn = document.getElementById('submit-btn');
		const textField = document.getElementById('text-field');
		
        myButtons.forEach(button => {
            button.addEventListener('click', function() {
                textField.value += this.dataset.number;
            });
		});

		eraseBtn.addEventListener('click', function() {
			textField.value = "";
		});
			
		submitBtn.addEventListener('click', function() {
			sendData(textField.value);
		});
        
		function sendData(code) {
				/* Trigger vibration if supported */
				/*if (navigator.vibrate && #hapticResponse#) { navigator.vibrate(100); } */

				const url = '#URL#';

				if (code !== null) {
					led1.classList.add('flicker');  /* Start flickering led1 */

					/* Construct the request payload */
					const payload = { code: code };
					
                    fetch(url, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(payload)
                    })
                    .then(response => {
						console.log('Data sent:', response.status); 
						console.log('Data:', response.data); 
            			if (!response.ok) {
                			throw new Error(`HTTP error! Status: ${response.status}`);
            			}
                        led1.classList.remove('flicker'); /* Turn off LED1 */
                        led2.style.color = 'green'; /* Set led2 to solid green */
                        setTimeout(() => {
                            led2.style.color ='#555'; /* Reset led2 to its original color */
                        }, 1000); /* Solid for 1 seconds */
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        led1.classList.remove('flicker'); /* Turn off LED1 */
                        led2.style.color = 'red'; /* Set led2 to solid red */
                        setTimeout(() => {
                            led2.style.color = '#555'; /* Reset led2 to its original color */
                        }, 1000); /* Solid for 1 seconds */
                    });
                }
                setTimeout(() => {
                    led1.classList.remove('flicker');
                }, 1000);
            }
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
		case 'btnHideInputHandling':
            state.hidden.InputHandling = state.hidden.InputHandling ? false : true
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
    if (section == "InputHandling") {
        if (state.hidden.InputHandling == true) return sectionTitle("Input Handling ▶") else return sectionTitle("Input Handling ▼")
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
			myCommandsList << commandName
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
    if (state.initialized == true) {
        if (isLogTrace) log.trace("<b>initialize: Initialize has already been run. Exiting</b>")
        return
    }
    log.trace("<b>Running Initialize</b>")

	//Keypad Title
	app.updateSetting("myTitle", "My Keypad Title")
	
	//Send Data
	app.updateSetting("sendDataTo", [value: "Device", type: "enum"])
	
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
    state.hidden = [Intro: false, Endpoints: true, Display: false, InputHandling: true, Publish: true]
    
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


