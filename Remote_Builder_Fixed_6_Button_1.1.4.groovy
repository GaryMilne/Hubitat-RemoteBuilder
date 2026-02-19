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
*  Original posting on Hubitat Community forum: https://community.hubitat.com/t/release-remote-builder-a-new-way-to-control-devices/142060
*  Remote Builder Documentation: https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf
*
*  Remote Builder 6 Button - ChangeLog
*
*  Gary Milne - September 2nd, 2024 @ 9:45 AM
*
*  Version 1.0.0 - Initial Public Release
*  Version 1.1.0 - Feature - Adds support for parameters to be passed to commands. Adds arithmetic operations to parameter fields. 
*  Version 1.1.1 - Feature - Adds remote Icon to Browser window.
*  Version 1.1.2 - Added Haptic Response option for button presses.
*  Version 1.1.3 - Added option for Unassigned Buttons to be Hidden, Disabled or Normal
*  Version 1.1.4 - Fixed bug with Tooltips not showing. Added ability to change tooltipTextSize. Removed excess logging information.
*
*  Possible Future Improvements:
*  Default button group to have open on load.
*
**/

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.transform.Field

static def buttonGroup() { return ['ONE', 'TWO', 'THREE'] }

@Field static final codeDescription = "<b>Remote Builder - 6 Button 1.1.4 (2/19/26 @ 8:57 AM)</b>"
@Field static final codeVersion = 114
@Field static final moduleName = "Custom 6 Button"
//@Field static final moduleName = "Fixed 6 Button"

definition(
        name: "Remote Builder - Custom 6 Button",
        description: "Generates a Custom 6 Button remote control that can operate be executed from a web browser or embedded into a Hubitat Dashboard.",
        importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Custom_6_Button.groovy",
	    //name: "Remote Builder - Fixed 6 Button",
        //description: "Generates a Fixed 6 Button remote control that can operate be executed from a web browser or embedded into a Hubitat Dashboard.",
        //importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Fixed_6_Button.groovy",
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
            	myText += "Both endpoints can be active at the same time and can be enabled or disabled through this interface.<br>"
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
				text += "When you make changes to properties (button color, text, text color or tooltip) of the remote they do not take effect until they have been Compiled.<br>"
				text += "Changes to commands or parameters take effect immediately and do not require the remote to be compiled.<br>"
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
				if (moduleName == "Custom 6 Button")  input(name: "selectedButtonGroup", type: "enum", title: bold("Button Group to Customize"), options: buttonGroup(), required: true, defaultValue: "ONE", submitOnChange: true, width: 2)
					
				if(selectedButtonGroup == "ONE") { 
					startIndex = 1; endIndex = 6; 
					input ("myRemoteBackground1", "color", title: "&nbsp<b>Group 1 Background Color</b>", required: false, defaultValue: "#555", width: 2, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")  
					input ("myTitleText1", "text", title: "&nbsp<b>Group 1 Title Text</b>", required: false, defaultValue: "#000", width: 2, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")  
					}
				if(selectedButtonGroup == "TWO") { 
					startIndex = 7; endIndex = 12;  
					input ("myRemoteBackground2", "color", title: "&nbsp<b>Group 2 Background Color</b>", required: false, defaultValue: "#833", width: 2, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")  
					input ("myTitleText2", "text", title: "&nbsp<b>Group 2 Title Text</b>", required: false, defaultValue: "#000", width: 2, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")  
					}
				if(selectedButtonGroup == "THREE") { 
					startIndex = 13; endIndex = 18; 
					input ("myRemoteBackground3", "color", title: "&nbsp<b>Group 3 Background Color</b>", required: false, defaultValue: "#7AD", width: 2, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
					input ("myTitleText3", "text", title: "&nbsp<b>Group 3 Title Text</b>", required: false, defaultValue: "#000", width: 2, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")  
					}
				input(name: "showParameters", type: "enum", title: bold("Show Parameters"), options: ['TRUE', 'FALSE'], defaultValue: "FALSE", submitOnChange: true, width: 2)								   
				
                paragraph line(1)
                
                (startIndex..endIndex).each { i ->
                    input ("myDevice$i", "capability.*", title: "<b>Button $i Device</b> ", multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px") /* top right bottom left */
                    input ("myCommand$i", "enum", title: "&nbsp<b>Command</b>", options: getCommandList(settings["myDevice$i"]), multiple: false, submitOnChange: true, width: 2, style: "margin: 2px 10px 2px 10px; padding:3px")
					if (showParameters == "TRUE") input ("myParameter$i", "text", title: "&nbsp<b>Parameter(s)</b>", multiple: false, submitOnChange: true, width: 1, style: "margin: 2px 10px 2px 10px; padding:3px")
					if (moduleName == "Custom 6 Button") { 
                    	input ("myButtonColor$i", "color", title: "&nbsp<b>Button Color</b>", required: false, width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
                    	input ("myText$i", "text", title: "&nbsp<b>Character</b>", multiple: false, submitOnChange: true, width: 1, required: true, style: "margin: 2px 10px 2px 10px; padding:3px;border: 1px solid gray")
                    	input ("myTextColor$i", "color", title: bold("&nbsp<b>Text Color</b>"), required: false, defaultValue: "#FFFFFF", width: 1, submitOnChange: true, style: "margin: 2px 10px 2px 10px; padding:3px")
					}
                    input ("myTooltip$i", "text", title: "&nbsp<b>Tooltip (optional)</b>", multiple: false, submitOnChange: true, width: 2, required: false, defaultValue: "?", style: "margin: 2px 10px 2px 10px; padding:3px; border: 1px solid gray;")
                    paragraph line(1)
                } 
				input(name: "unassignedButtonBehaviour", type: "enum", title: bold("Unassigned Button Behaviour"), options: ["Normal", "Disabled", "Hidden"], required: false, defaultValue: "Normal", submitOnChange: true, width: 2, newLine: true, style:"margin-right: 20px")
				input(name: "enableHapticResponse", type: "enum", title: bold("Enable Haptic Response"), options: ["True", "False"], required: false, defaultValue: "False", submitOnChange: true, width: 2, style:"margin-right: 20px")
                input(name: "tooltipTextSize", type: "enum", title: bold("Tooltip Text Size"), options: ["3vh", "4vh", "5vh", "6vh", "7vh", "8vh", "9vh", "10vh"], required: false, defaultValue: "6vh", submitOnChange: true, width: 2)
				
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
            input(name: "myRemote", title: "<b>Attribute to store the Remote?</b>", type: "enum", options: parent.allTileList(), required: false, submitOnChange: true, width: 2, defaultValue: 0, newLine: false)
            input(name: "myRemoteName", type: "text", title: "<b>Name this Remote</b>", submitOnChange: true, width: 3, newLine: false, defaultValue: "New 6 Button Remote", required: false)
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
				input(name: "isLogConnections", type: "bool", title: "<b>Log all connection requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
				input(name: "isLogActions", type: "bool", title: "<b>Log all action requests?</b>", defaultValue: false, submitOnChange: true, width: 3)
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
        //def index = (i)
		isActive = true
		isHidden = false
		
		if ( settings."myDevice$i" == null || settings."myCommand$i" == null ) isActive = false
		if (isLogDebug) log.debug ("Index: $i  and isActive: $isActive and myCommand is: " + settings."myCommand$i")
		
		switch(unassignedButtonBehaviour){
        	case ["Normal"]:  //Show all buttons
				data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "tooltip": settings."myTooltip$i", "bHidden": "false" ]
    	        break
        	case ["Disabled"]:  //Buttons are shown but are disabled
				if (isActive) data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "tooltip": settings."myTooltip$i", "bHidden": "false"  ]
				else data = [ "index": "$i", "label": "?", "bColor": "#555", "tColor": "#555", "bHidden": "false"]
        	    break
			case ["Hidden"]:
				if (isActive) data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "tooltip": settings."myTooltip$i", "bHidden": "false"  ]
				else data = [ "index": "$i", "label": settings."myText$i", "bColor": settings."myButtonColor$i", "tColor": settings."myTextColor$i" , "tooltip": settings."myTooltip$i", "bHidden": "true"  ]
    	        break
        	default:
            	break
		}
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
	content = content.replace("#hapticResponse#", enableHapticResponse.toLowerCase() )
    content = content.replace("#tooltipTextSize#", tooltipTextSize.toLowerCase() )
	
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
	
	//Remove unneccessary spaces
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
	myParameter = settings["myParameter$i"]
	
	// Record the action request
    if (isLogActions) log.info ( "Remote Builder Data Received - Remote: $myRemoteName - Button: $i - Device: $myDevice - Command: $myCommand - Parameters: $myParameter")
	
	if (myDevice == null || myCommand == null) return
	
	//This checks to see if the command end in any of the numbers 1-4.  If it does it sets the myCommandIndex to the value found. This is used for things like button presses.
    def myCommandIndex = 0
    if (myCommand && myCommand[-1] in '1'..'4') {
        myCommandIndex = myCommand[-1] as int
    }
	
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
	if (isLogDebug) log.debug ("assembleCommands: '$myCommand' -  '$myParameters' ")
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
    <title>Remote Control</title>
	<link rel="icon" href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAAsTAAALEwEAmpwYAAABC0lEQVR4nJXTuy5FQRTG8R8hPINLgqcQl4gSjUQhGh5AQaUUj6IUDR1egEZQuMQlQRA6CTmNhkyyNmPnHGf7kknWrJn/N2tWZvifxrGBFbRWhVrQEXF7zKewXsWkF+eoYQbD2MEgprHcDL7BZ4yPMOkMk1TJZiO4J4Mf8FwyGUIbxhrB1wEsZn1YK5nUVXcGn0ZuEqNh8hJrtayxv+Cr7M7HkZ/FRMSPsXYRht/qyuAjvEY897PFUuRu0VeGL7OT57GXzU9w1ghOOozFfaxm4G5WSRp39eCkp9hwj+0MWIg+/AnDCN4zcKtUyZ9wbvKWAQdZ3N8MLpReV2FSXGmgKlwofZb0WFLXK538BaLoXBuHlaWwAAAAAElFTkSuQmCC" type="image/png">
    <style>
        html, body { align-items:center; display:flex; font-family:Arial; sans-serif; font-size:20px; justify-content:center; padding:0; user-select:none; height:100vh; background-color:transparent; }
        .container { background-color:transparent; padding:0px; border-radius:4px;position:relative; height:80%; overflow:hidden}
        .control { cursor:pointer }
        .no-cursor { font-size:100%; font-weight:bold; pointer-events:none; text-anchor:middle; }
        .hidden { display:none }
		.tooltip { background-color:yellow; opacity: 0.75; border-radius:4px; font-size:#tooltipTextSize#; color:black; display:none; left:50%; padding:2px; pointer-events:none; position:absolute; text-align:center; top:5%; transform: translateX(-50%); width:90%; }		
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
            helpButtonText.setAttribute('fill', helpMode ? 'orange' : '#fff');
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
			if (button && button.bHidden === 'true') { buttonElement.style.display = "none"; textElement.style.display = "none"; }
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

	/* Trigger a vibration for 200 milliseconds if the user has selected it and it is supported by the device. */
	if (navigator.vibrate && #hapticResponse#) { navigator.vibrate(100); } 
	
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
            }, 1000); /* Solid for 1 second */
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
		app.updateSetting("myParameter$i", "?")
        app.updateSetting("myText$i", buttonTextList[i - 1] )
        app.updateSetting("myTooltip$i", "?" )
		if (moduleName == "Fixed 6 Button" )  app.updateSetting("myButtonColor$i", , [value:  "#888888" , type: "color"])
		if (moduleName == "Custom 6 Button" ) app.updateSetting("myButtonColor$i", [value: buttonColorList[ i - 1 ], type: "color"] )
        app.updateSetting("myButtonColor$i", buttonColorList[ i - 1 ] )
        app.updateSetting("myTextColor$i", [value: "#FFFFFF", type: "color"])
        app.updateSetting("selectedButtonGroup", [value: "ONE", type: "enum"])
    }
	
	app.updateSetting("showParameters", [value: "FALSE", type: "enum"])
	app.updateSetting("unassignedButtonBehaviour", [value: "Normal", type: "enum"])
	
	//Remote Settings
	if (moduleName == "Fixed 6 Button" ) app.updateSetting("myRemoteBackground1", [value: "#333333", type: "color"])
	if (moduleName == "Custom 6 Button" ) app.updateSetting("myRemoteBackground1", [value: "#A05050", type: "color"])
	app.updateSetting("myRemoteBackground2", [value: "#506090", type: "color"])
	app.updateSetting("myRemoteBackground3", [value: "#509090", type: "color"])
	app.updateSetting("myTitleText1", "Group 1" )
	app.updateSetting("myTitleText2", "Group 2" )
	app.updateSetting("myTitleText3", "Group 3" )
	app.updateSetting("enableHapticResponse", [value: "False", type: "enum"])
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

