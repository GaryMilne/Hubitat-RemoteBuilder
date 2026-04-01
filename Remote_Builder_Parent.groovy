/**
*  Remote Builder Parent App
*  Version: See ChangeLog
*  Download: See importUrl in definition
*  Description: Used in conjunction with child apps to generate tabular reports on device data and publishes them to a dashboard.
*
*  Copyright 2022 Gary J. Milne  
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
*  Remote Builder Documentation: TBD
*
*  Remote Builder Parent App - ChangeLog
*  Version 1.0.0 - Limited Release
*  Version 1.0.1 - Fixed checkLicense for handling Hub migrations
*  Version 1.0.2 - Added Roku Module.
*  Version 1.1.0 - Added Saving and Removal of Remote Links for each published remote.
*  Version 1.1.1 - Added Keypad Module
*  Version 1.1.2 - Added QR Code Generator
*  Version 1.1.3 - Added SmartGrid
*  Version 1.1.4 - Shortened code by pointing to an external image for the Roku Remote shown in the introduction tab. Link points to post in Hubitat Community.
*  Version 2.0.0 - Added remoteBuilderTemplate() function to hold the code for the Remote Builder template. This makes the Remote Builder code size more manageable.
*  				 - Add sub-group sorting to the SmartGrid JS template. Added color coding for some numeric values to the template. SmartGrid Template changes.
*
*  Gary Milne - 03/30/26 @ 2:11 PM
*
**/

import groovy.transform.Field
@Field static final codeDescription = "<b>Remote Builder Parent v2.0.0 (03/30/26)</b>"
@Field static final codeVersion = 200

//These are the data for the pickers used on the child forms.
def storageDevices() { return ['Remote Builder Storage Device 1', 'Remote Builder Storage Device 2', 'Remote Builder Storage Device 3'] }
def allTileList() { return [1:'Remote1', 2:'Remote2', 3:'Remote3', 4:'Remote4', 5:'Remote5', 6:'Remote6', 7:'Remote7', 8:'Remote8', 9:'Remote9', 10:'Remote10', \
							11:'Remote11', 12:'Remote12', 13:'Remote13', 14:'Remote14', 15:'Remote15', 16:'Remote16', 17:'Remote17', 18:'Remote18', 19:'Remote19', \
							20:'Remote20', 21:'Remote21', 22:'Remote22', 23:'Remote23', 24:'Remote24', 25:'Remote25'] }
definition(
    name: 'Remote Builder',
    namespace: 'garyjmilne',
    author: 'Gary Milne',
    description: 'Remote Builder Parent App',
    category: 'Dashboards',
	importUrl: "https://raw.githubusercontent.com/GaryMilne/Hubitat-RemoteBuilder/main/Remote_Builder_Parent.groovy",
    iconUrl: '',
    iconX2Url: '',
    iconX3Url: '',
    singleThreaded: true,
    installOnOpen: true
    )
	
preferences {
    page name: 'mainPage', title: '', install: true, uninstall: true // ,submitOnChange: true
}

def mainPage() {
    //if (state.initialized == null ) initialize()
    if (state.initialized == null || state.variablesVersion == null || state.variablesVersion < codeVersion) initialize()
        
    dynamicPage(name: "mainPage") {
        //See if the user has changed the selected storage device
        isSelectedDeviceChanged()

        //Refresh the UI - neccessary for controls located on the same page.
        refreshUI()

        //This is all a single section as section breaks have been commented out. This uses less screen space.
        section { 
            paragraph "<div style='text-align:center;color: #c61010; font-size:30px;text-shadow: 0 0 5px #FFF, 0 0 10px #FFF, 0 0 15px #FFF, 0 0 20px #49ff18, 0 0 30px #49FF18, 0 0 40px #49FF18, 0 0 55px #49FF18, 0 0 75px #ffffff;;'> Remote Builder 📱</div>"
            //Intro
            if (state.showIntro == true || state.setupState == 1) {
                input(name: 'btnShowIntro', type: 'button', title: 'Introduction ▼', backgroundColor: 'navy', textColor: 'white', submitOnChange: true, width: 2)  //▼ ◀ ▶ ▲
                
                myString = "With <b>Remote Builder</b> you can create virtual Remote Controls that can be used to control devices around the home. These remotes can be installed onto the Hubitat Dashboard but they can also be accessed "
				myString += "directly from any phone, tablet or computer around the home or around the world. "
				myString += "Virtual remotes are the digital equivalent of a physical remote control, but one that can be customed to perform any action. It provides a much simpler and more intuitive interface than using a button device on the current Hubitat Dashboard. "
                if (state.setupState != 99) titleise2(red("<b>First time setup!</b>"))
                paragraph myString
                
                myString = "You are installing <b>Remote Builder Standard which is free.</b> The standard version gives access to the <b>Fixed 6 Button Remote, Roku Remote, Keypad and QR Code Generator</b>.<br>"
				myString += "The Advanced version gives you access to the <b>Custom 6 Button Remote, TV Remote and SmartGrid</b>.<br>"
                myString += "If you wish to upgrade to <b>Remote Builder Advanced</b> you can do so after setup is complete by visiting the Licensing section. The Advanced version gives you access to a variety of Remotes.<br>"
				myString += "<b>Remote Builder</b> and <b>Tile Builder</b> are sister products. An Advanced license for either one grants you full access to the other."
                paragraph myString
				
				//Display the sample from the community forum
                myHTML = """<img src="https://community.hubitat.com/uploads/default/original/3X/3/e/3eb8aec242f1c0de0207d48c02fde7f79f15be85.png" style="max-height:300px; width:auto;">"""
                paragraph myHTML
                                
                if (state.setupState != 99) myText = "  Use the <b>Next</b> button to move through the sections for initial setup."
                else myText = "<b>Click on the section headers to navigate to a section.</b>"
                paragraph(myText)
                
                //Only show button during the setup process
                if (state.setupState != 99) {
                    input(name: 'btnNext1', type: 'button', title: 'Next ▶', backgroundColor: 'teal', textColor: 'white', submitOnChange: true, width: 2)  //▼ ◀ ▶ ▲
                }
            }
            else {
                input(name: 'btnShowIntro', type: 'button', title: 'Introduction ▶', backgroundColor: 'DodgerBlue', textColor: 'white', submitOnChange: true, width: 2)  //▼ ◀ ▶ ▲
            }
            paragraph line(2)
            //End of Intro
            
            
            //Licensing
            if (state.setupState == 99) {
                if (state.showLicense == true) {
                    input(name: 'btnShowLicense', type: 'button', title: 'Licensing ▼', backgroundColor: 'navy', textColor: 'white', submitOnChange: true, width: 2, newLineBefore: true, newLineAfter: false)  //▼ ◀ ▶ ▲
                    link1 = 'Click <a href="https://github.com/GaryMilne/Hubitat-RemoteBuilder/blob/main/Remote%20Builder%20Help.pdf" target="_blank">here</a> for more information.'
                                        
                    myString = "<b>Remote Builder Standard (Free)</b><br>"
                    myString += "<b>1)</b> Fixed 6 Button Remote. Button actions can be customized but the interface cannot be.<br><br>"
					myString += "<b>2)</b> Roku Remote with 4 custom buttons.<br><br>"
					myString += "<b>3)</b> Keypad with keys 0-9, * and #. Data can be passed to Rules, Hub variables or driver commands.<br><br>"
					myString += "<b>4)</b> QR Code Generator for putting QR codes on a dashboard.<br><br>"
                    myString += "<b>Remote Builder Advanced (Donation Required)</b><br>"
                    myString += "<b>1)</b> Customizable 6 Button Remote with 3 X button groups (18 commands). Highly customizable for button color, text, tooltips and background.<br>"
					myString += "<b>2)</b> Universal TV Remote shown earlier with user defineable buttons for quick access.<br>"
					myString += "<b>3)</b> SmartGrid mixes elements of Tile Builder with full control of the devices.<br>"
					myString += "<b>4)</b> More to come!<br><br>"
                    myString += titleise("Remote Builder is bundled with Tile Builder and uses the same licensing keys. If you already have Tile Builder Advanced you can use those keys to activate Remote Builder Advanced.")
                    
                    myString = myString + "To purchase the license for <b>Remote Builder \\ Tile Builder Advanced</b> you must do the following:<br>"
                    myString += '<b>1)</b> Donate at least <b>\$12</b> to ongoing development of Tile Builder \\ Remote Builder via PayPal using this <a href="https://www.paypal.com/donate/?business=YEAFRPFHJCTFA&no_recurring=1&item_name=A+donation+of+%2412+or+more+grants+a+license+to+Remote+Builder+and+Tile+Builder+Advanced.+Leave+your+Hubitat+Community+ID&currency_code=USD" target="_blank">link.</a></br>'			
                    myString += "<b>2)</b> Forward the paypal eMail receipt along with your ID (<b>" + getID() + "</b>) to <b>TileBuilderApp@gmail.com</b>. Please include your Hubitat community ID for future notifications.<br>"
                    myString += "<b>3)</b> Wait for license key eMail notification (usually within 24 hours).<br>"
                    myString += "<b>4)</b> Apply license key using the input box below.<br>"
                    myString += "<b>Please respect the time and effort it took to create this application and comply with the terms of the license.</b>"
                    paragraph note('', myString)
			
                    if (state.isAdvancedLicense == false ){
                        input (name: "licenseKey", title: "<b>Enter Advanced License Key</b>", type: "string", submitOnChange:true, width:3, defaultValue: "?")
                        input (name: 'activateLicense', type: 'button', title: 'Activate Advanced License', backgroundColor: 'orange', textColor: 'black', submitOnChange: true, width: 2)
                        myString = '<b>Activation State: ' + red(state.activationState) + '</b><br>'
                        myString = myString + 'You are running ' + dodgerBlue('<b>Remote Builder Standard</b>')
                        paragraph myString
                    }
                    else {
                        myString = '<b>Activation State: ' + green(state.activationState) + '</b><br>'
                        myString = myString + 'You are running ' + green('<b>Remote Builder Advanced</b>')
                        paragraph myString
                    }
                }
            else {
                input(name: 'btnShowLicense', type: 'button', title: 'Licensing ▶', backgroundColor: 'DodgerBlue', textColor: 'white', submitOnChange: true, width: 2)  //▼ ◀ ▶ ▲
                }
            paragraph line(2)
            }
            //End of Licensing
            
            //Device
            if (state.showDevice == true ) {
                input(name: 'btnShowDevice', type: 'button', title: 'Storage Device ▼', backgroundColor: 'navy', textColor: 'white', submitOnChange: true, width: 2, newLineBefore: true)  //▼ ◀ ▶ ▲
                paragraph "<b>Remote Builder</b> stores generated tiles on a special purpose <b>Remote Builder Storage Device</b>. You must <b>create a device and attach</b> to it using the controls below.<br>"                 
                paragraph note('Note: ', "Each instance of <b>Remote Builder</b> must have its own unique storage device but can store up to 25 remotes.")
                    
                if (state.isStorageConnected == false ) {
                    paragraph red('❌ - A Remote Builder Storage Device is not connected.')
                    myString = "You do not have a 'Remote Builder Storage Device' connected. Click the button below to create\\connect one. <br>"
                    myString += "<b>Important: </b>If you remove the <b>Remote Builder</b> App the Remote Builder Storage Device will become orphaned and unusable. <br>"
                    myString += "<b>Note: </b>It is possible to install multiple instances of <b>Remote Builder</b>. In such a scenario each instance should be connected to a unique Remote Builder Storage Device."
                    
                    input(name: 'selectedDevice', type: 'enum', title: bold('Select a Remote Builder Storage Device'), options: storageDevices(), required: false, defaultValue: 'Remote Builder Storage Device 1', submitOnChange: true, width: 3, newLineAfter:true)
                    input(name: 'createDevice', type: 'button', title: 'Create Device', backgroundColor: 'MediumSeaGreen', textColor: 'white', submitOnChange: true, width: 2)
                    
                    if (state.isStorageConnected == false) input(name: 'connectDevice', type: 'button', title: 'Connect Device', backgroundColor: 'Orange', textColor: 'white', submitOnChange: true, width: 2)
                    else input(name: 'doNothing', type: 'button', title: 'Connect Device', backgroundColor: 'MediumSeaGreen', textColor: 'white', submitOnChange: true, width: 2)
                            
                    input(name: 'deleteDevice', type: 'button', title: 'Delete Device', backgroundColor: 'Maroon', textColor: 'yellow', submitOnChange: true, width: 2, newLineAfter: true)
                    if (state.hasMessage != null && state.hasMessage != '' ) {
                        if (state.hasMessage.contains("Error")) paragraph note('', red(state.hasMessage))
                        else paragraph note('', green(state.hasMessage))
                    }
                }
                else {
                    paragraph green('✅ - ' + state.myStorageDevice + ' is connected.')
                    paragraph note('', 'You have successfully connected to a Remote Builder Storage Device on your system. You can now create and publish tiles.')
                    input(name: 'disconnectDevice', type: 'button', title: 'Disconnect Device', backgroundColor: 'orange', textColor: 'black', submitOnChange: true, width: 2, newLineAfter: true)
                    }
                //Only show button during the setup process
                if (state.setupState != 99)
                    input(name: 'btnNext2', type: 'button', title: 'Next ▶', backgroundColor: 'teal', textColor: 'white', submitOnChange: true, width: 2, newLine: true)  //▼ ◀ ▶ ▲
                }
            else input(name: 'btnShowDevice', type: 'button', title: 'Storage Device ▶', backgroundColor: 'DodgerBlue', textColor: 'white', submitOnChange: true, width: 2)  //▼ ◀ ▶ ▲
            paragraph line(2)
            //End of Device
                    
            //Setup Complete
            if (state.setupState == 3){
                paragraph titleise2(green('The required steps for setup are now complete!<br>'))
                paragraph 'Click <b>Finish Setup</b> to proceed to creating your first tile!'
                paragraph note("Note: ", "From now on you can click on the section headers to navigate the configuration options.")
                input(name: 'btnNext3', type: 'button', title: 'Finish Setup ▶', backgroundColor: 'teal', textColor: 'white', submitOnChange: true, width: 2)  //▼ ◀ ▶ ▲
                paragraph line(2)
                }
            //End of Setup
            
            //Create Tiles
            if (state.setupState == 99) {
                if (state.showCreateEdit == true) {
                    //if (true ){
                    input(name: 'btnShowCreateEdit', type: 'button', title: 'Create\\Edit Remotes ▼', backgroundColor: 'navy', textColor: 'white', submitOnChange: true, width: 2, newLineBefore: true, newLineAfter: false)  //▼ ◀ ▶ ▲
                    myString = '<b>Remote Builder</b> currently has five types of remotes:<br>'
                    myString += '<b>1)</b> Fixed 6 Button Remote. (Standard)<br>'
					myString += '<b>2)</b> Roku Remote. (Standard)<br>'
					myString += '<b>3)</b> Keypad. (Standard)<br>'
					myString += '<b>4)</b> QR Code Generator. (Standard)<br>'
                    myString += '<b>5)</b> Custom 6 Button Remote with 3 x button groups for 18 unique actions. (Advanced)<br>'
                    myString += '<b>6)</b> TV Remote with custom buttons. (Advanced)<br>'
					myString += '<b>7)</b> SmartGrid. (Advanced)<br>'
					myString += '<b>More to come.</b>'
                    paragraph note('', myString)
                    
					//Advanced only items use the checkLicense() function to tell if they should be displayed.
                    if (!hideFixed6ButtonRemote) app (name: 'TBPA', appName: 'Remote Builder - Fixed 6 Button', namespace: 'garyjmilne', title: 'Add Fixed Six Button Remote')
					if (!hideCustom6ButtonRemote && checkLicense() ) app (name: 'TBPA', appName: 'Remote Builder - Custom 6 Button', namespace: 'garyjmilne', title: 'Add Custom Six Button Remote')
					if (!hideKeypad) app (name: 'TBPA', appName: 'Remote Builder - Keypad', namespace: 'garyjmilne', title: 'Add Keypad')
					if (!hideRokuRemote) app (name: 'TBPA', appName: 'Remote Builder - Roku', namespace: 'garyjmilne', title: 'Add Roku Remote')
					if (!hideQRCode) app (name: 'TBPA', appName: 'Remote Builder - QR Code', namespace: 'garyjmilne', title: 'Add QR Code')
					if (!hideTVRemote && checkLicense() ) app (name: 'TBPA', appName: 'Remote Builder - TV', namespace: 'garyjmilne', title: 'Add TV Remote')
					if (!hideSmartGrid && checkLicense() ) app (name: 'TBPA', appName: 'Remote Builder - SmartGrid', namespace: 'garyjmilne', title: 'Add SmartGrid')
                    }
                else {
                    input(name: 'btnShowCreateEdit', type: 'button', title: 'Create\\Edit Remotes ▶', backgroundColor: 'DodgerBlue', textColor: 'white', submitOnChange: true, width: 2, newLineBefore: true, newLineAfter: false)  //▼ ◀ ▶ ▲
                }
                paragraph line(2)
            }
            //End of Create Tiles
        
            //Manage Remotes 
            if (state.setupState == 99) {
                if (state.showManage == true ) {
                    input(name: 'btnShowManage', type: 'button', title: 'Manage Tiles ▼', backgroundColor: 'navy', textColor: 'white', submitOnChange: true, width: 2, newLineBefore: true, newLineAfter: false)  //▼ ◀ ▶ ▲
                    myString = 'Here you can view information about the tiles on this storage device, which tiles are in use, the last time those tiles were updated and delete obsolete tiles.<br>'
                    myString += 'In the <b>Remote Builder Storage Device</b> you can also preview the tiles, add descriptions and delete tiles as necessary.'
                    paragraph note('Note: ', myString)
                    input name: 'tilesInUse', type: 'enum', title: bold('List Tiles in Use'), options: getTileList(), required: false, defaultValue: 'Tile List', submitOnChange: false, width: 4, newLineAfter:true
                    /*input name: 'tilesInUseByActivity', type: 'enum', title: bold('List Tiles By Activity'), options: getTileListByActivity(), required: false, defaultValue: 'Tile List By Activity', submitOnChange: true, width: 4, newLineAfter:true*/
		    		input(name: 'deleteTile', type: 'button', title: '↑ Delete ↑ Selected ↑ Tile ↑', backgroundColor: 'Maroon', textColor: 'yellow', submitOnChange: true, width: 2)
			    	paragraph note('Note: ', 'Deleting a tile does not delete the <b>Remote Builder</b> child app that generates the tile. Delete the child app first and then delete the tile.')
                }
                
            else {
                input(name: 'btnShowManage', type: 'button', title: 'Manage Tiles ▶', backgroundColor: 'DodgerBlue', textColor: 'white', submitOnChange: true, width: 2, newLineBefore: true, newLineAfter: false)  //▼ ◀ ▶ ▲
                }
            paragraph line(2)
            }
            //End of Manage  
       
            //More
            if (state.setupState == 99) {
                if (state.showMore == true) {
                    input(name: 'btnShowMore', type: 'button', title: 'More ▼', backgroundColor: 'navy', textColor: 'white', submitOnChange: true, width: 2, newLineBefore: true, newLineAfter: true)  //▼ ◀ ▶ ▲
                    label title: bold('Enter a name for this Remote Builder parent instance (optional)'), required: false, width: 4, newLineAfter: true
                    
                    paragraph body('<b>Logging Functions</b>')
                    input (name: "isLogInfo",  type: "bool", title: "<b>Enable info logging?</b>", defaultValue: false, submitOnChange: true, width: 2)
                    input (name: "isLogTrace", type: "bool", title: "<b>Enable trace logging?</b>", defaultValue: false, submitOnChange: true, width: 2)
                    input (name: "isLogWarn",  type: "bool", title: "<b>Enable warn logging?</b>", defaultValue: true, submitOnChange: true, width: 2)
                    input (name: "isLogError",  type: "bool", title: "<b>Enable error logging?</b>", defaultValue: true, submitOnChange: true, width: 2, newLineAfter: true)
                    paragraph line(1)
                    
                    paragraph body('<b>Show/Hide Modules</b>')
                    input (name: "hideFixed6ButtonRemote", type: "bool", title: "<b>Hide Fixed 6 Button Remote?</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideCustom6ButtonRemote",  type: "bool", title: "<b>Hide Custom 6 Button Remote</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideRokuRemote", type: "bool", title: "<b>Hide Roku Remote?</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideTVRemote", type: "bool", title: "<b>Hide TV Remote?</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideKeypad",  type: "bool", title: "<b>Hide Keypad</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideQRCode",  type: "bool", title: "<b>Hide QR Code</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideSmartGrid",  type: "bool", title: "<b>Hide SmartGrid</b>", defaultValue: false, submitOnChange: true, width: 2)
                    paragraph line(1)
                    
                    input(name: 'removeLicense'  , type: 'button', title: 'De-Activate Software License', backgroundColor: '#27ae61', textColor: 'white', submitOnChange: true, width: 3, newLineAfter: true)
                    input(name: 'rebuildSmartGridTemplate'  , type: 'button', title: 'Rebuild SmartGrid Template', backgroundColor: '#27ae61', textColor: 'white', submitOnChange: true, width: 3, newLineAfter: true)
                }
                else {
                    input(name: 'btnShowMore', type: 'button', title: 'More ▶', backgroundColor: 'DodgerBlue', textColor: 'white', submitOnChange: true, width: 2)  //▼ ◀ ▶ ▲
                }
            paragraph line(2)
            }
            //End of More
			
			//Now add a footer.
            myText = '<div style="display: flex; justify-content: space-between;">'
            myText += '<div style="text-align:left;font-weight:small;font-size:12px"> Developer: Gary J. Milne</div>'
            myText += '<div style="text-align:center;font-weight:small;font-size:12px">Version: ' + codeDescription + '</div>'
            myText += '<div style="text-align:right;font-weight:small;font-size:12px">Copyright 2024</div>'
            myText += '</div>'
            paragraph myText  
           //paragraph ("setupState is: $state.setupState")
           //input(name: "test"  , type: "button", title: "test", backgroundColor: "#27ae61", textColor: "white", submitOnChange: true, width: 2, newLineAfter: false)
        }
        
    }
        
        //Refresh the UI - neccessary for controls located on the same page.
        //log.info ("Refresh again")
        //refreshUI()
    }

//Receives the salient properties of a remote and saves them to state.
def saveRemoteLinks(Map inputMap){
	def number = inputMap.number
	def name = inputMap.name
    def localEndpoint = inputMap.localEndpoint
    def cloudEndpoint = inputMap.cloudEndpoint
	state["Remote${number}"] = inputMap
	if (isLogDebug) log.debug ("Remote: Number: $number  -  Name: $name  -  localEndpoint: $localEndpoint  -  cloudEndpoint: $cloudEndpoint ")
}


//Returns a short version of the Storage Device Name for this instance.
def getStorageShortName(){
    if (isLogInfo) log.info ("Storage Name is: ${state.myStorageDeviceDNI.toString()} ")
    if (state.myStorageDeviceDNI == "Remote_Builder_Storage_Device_1" ) return "RBSD1"
    if (state.myStorageDeviceDNI == "Remote_Builder_Storage_Device_2" ) return "RBSD2"
    if (state.myStorageDeviceDNI == "Remote_Builder_Storage_Device_3" ) return "RBSD3"
}

//Returns a long version of the Storage Device Name for this instance.
def getStorageLongName(){
    return state.myStorageDeviceDNI.toString()
}

//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//**************
//**************  Setup and UI Functions
//**************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************

//Show the selected section and hide all the others.
def showSection(section, override){
    //if (state.inSetup == true && override == false) return
    state.showIntro = false
    state.showLicense = false
    state.showDevice = false
    state.showSetupComplete = false
    state.showCreateEdit = false
    state.showManage = false
    state.showMore = false
    
    if (section == "Intro" ) state.showIntro = true
    if (section == "License" ) state.showLicense = true
    if (section == "Device" ) state.showDevice = true
    if (section == "SetupComplete" ) state.showSetupComplete = true
    if (section == "CreateEdit" ) state.showCreateEdit = true
    if (section == "Manage" ) state.showManage = true
    if (section == "More" ) state.showMore = true
}

//This is the standard button handler that receives the click of any button control.
def appButtonHandler(btn) {
    switch (btn) {
        case 'test':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on test')
            test()
            break
        case 'btnNext1':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnNext1')
            state.setupState = 2
            showSection("Device", true)
            break
        case 'btnNext2':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnNext2')
            state.setupState = 3
            showSection("SetupComplete", true)
            break
        case 'btnNext3':
            state.setupState = 99
            showSection("CreateEdit", true)
            break
        case 'btnShowIntro':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnShowIntro')
            showSection("Intro", false)
            break
        case 'btnShowLicense':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnShowLicense')
            showSection("License", false)
            break
        case 'btnShowDevice':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnShowDevice')
            showSection("Device", false)
            break
        case 'btnShowCreateEdit':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnShowCreateEdit')
            showSection("CreateEdit", false)
            break
        case 'btnShowManage':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnShowManage')
            showSection("Manage", false)
            break
        case 'btnShowMore':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on btnShowMore')
            showSection("More", false)
            break
        case 'createDevice':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on createDevice')
            makeTileStorageDevice()
            break
        case 'connectDevice':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on connectDevice')
            connectTileStorageDevice()
            break
        case 'disconnectDevice':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on disconnectDevice')
            disconnectTileStorageDevice()
            break
        case 'deleteDevice':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on deleteDevice')
            deleteTileStorageDevice()
            break
		case 'deleteTile':
            if (isLogTrace) log.trace('appButtonHandler: Clicked on deleteTile')
            deleteTile()
            break
        case 'doNothing':
            break
        case 'activateLicense': 
            if (isLogTrace) log.trace('appButtonHandler: Clicked on activateLicense')
            if (activateLicense() == true ) state.activationState = "Success"
            else state.activationState = "Failed"
            break
        case 'removeLicense': 
            if (isLogTrace) log.trace('appButtonHandler: Clicked on removeLicense')
            state.isAdvancedLicense = false
            state.activationState = "Not Activated"
            break
        case 'rebuildSmartGridTemplate':
        	if (isLogTrace) log.trace('appButtonHandler: Rebuild SmartGrid Template')
        	state.remove("smartGridTemplate")
        	state.smartGridTemplate = buildSmartGridTemplate()
        	break
    }
}

//This is called after a submit actions
void refreshUI() {
    if (selectedDevice == null ) selectedDevice = 'Remote Builder Storage Device 1'
    if (state.flags.selectedDeviceChanged == true && selectedDevice != null) {
        state.isStorageConnected = false
        if (selectedDevice != null ) log.info('selectedDevice is:' + selectedDevice)
        state.myStorageDevice = selectedDevice
        state.myStorageDeviceDNI = selectedDevice.replace(' ', '_')
        state.hasMessage = '<b>You must connect to a storage device in order to publish tiles.</b>'
        
    }
}


//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//**************
//**************  Storage Device Functions
//**************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************

//Called by the child apps. Returns an open handle to the childDevice
def getStorageDevice() {
    if (state.isStorageConnected == true ) {
        deviceDNI = state.myStorageDeviceDNI
        myStorageDevice = getChildDevice(deviceDNI)
        if (isLogDebug) log.debug("Parent returning myStorageDevice is: $myStorageDevice")
        return    myStorageDevice
    }
    else {
        log.warn('getStorageDevice: There is no storage device connected.')
        return null
    }
}

//Create a Remote Builder Storage Device.
def makeTileStorageDevice() {
    if (isLogTrace) log.trace("makeTileStorageDevice: Attempting to create Remote Builder Storage Device: $selectedDevice")
    deviceName = selectedDevice.toString()
    deviceDNI = deviceName.replace(' ', '_')
    try {
        myChildDevice = addChildDevice('garyjmilne', 'Remote Builder Storage Driver', deviceDNI, [ isComponent:false, label: deviceName, name: deviceName] )
        if (myChildDevice) {
            if (isLogInfo) log.info ("makeTileStorageDevice: <b>Storage Device ${state.myStorageDevice} Created.</b>")
            state.hasMessage = "<b>Storage Device ${state.myStorageDevice} Created. You must connect to it before you can start publishing tiles.</b>"
            state.myStorageDevice = deviceName
            state.myStorageDeviceDNI = deviceDNI
        }
         else {
            log.warn = ("makeTileStorageDevice(): Device Creation Error! Does the device '$deviceName' already exist? Was it created by a different instance of Remote Builder?")
            state.hasMessage = "<b>makeTileStorageDevice(): Device Creation Error! Does the device '$deviceName' already exist? Was it created by a different instance of Remote Builder?</b>"
         }
    }
    catch (ex) {
        log.error('makeTileStorageDevice(): Device Creation Error! Does the device already exist.')
        state.hasMessage = '<b>Device Creation Error! Does the device already exist? Was it created by a different instance of Remote Builder?</b>'
        state.isStorageConnected = false
    }
}

//Connect to an existing Tile Storage Device
def connectTileStorageDevice() {
    if (isLogTrace) log.trace ('connectTileStorageDevice: Entering connectTileStorageDevice')
    deviceDNI = state.myStorageDeviceDNI
    if (isLogDebug) log.debug("Connecting to Storage Device: $deviceDNI")
    try {
        myChildDevice = getChildDevice(deviceDNI)
        if (isLogDebug) log.debug ("myChildDevice is: $myChildDevice")
        if (myChildDevice != null) {
            state.hasMessage = "connectTileStorageDevice(): Connect Success ($myChildDevice)"
            if (isLogInfo) log.info ("connectTileStorageDevice(): Connect Success ($myChildDevice)")
            state.isStorageConnected = true
        }
         else {
            state.hasMessage = '<b>Device Connection Error! Does the device exist? Was it created by a different instance of Remote Builder?</b>'
            state.isStorageConnected = false
         }
    }
    catch (ex) {
        log.error("connectTileStorageDevice(): Failed - $selectedDevice. Exception:$ex")
        state.hasMessage = "<b>Exception encountered. Connection to '${selectedDevice}' failed.</b>"
        state.isStorageConnected = false
    }
}

//Disonnect from an existing Tile Storage Device
def disconnectTileStorageDevice() {
    if (isLogTrace) log.trace ('disconnectTileStorageDevice: Entering disconnectTileStorageDevice')
    deviceDNI = state.myStorageDeviceDNI
    if (isLogInfo) log.info("Disconnecting from Storage Device: $deviceDNI")
    try {
        myChildDevice = getChildDevice(deviceDNI)
        if (myChildDevice == true ) {
            if (isLogInfo) log.info("disconnectTileStorageDevice(): Successfully disconnected from $myChildDevice")
            state.hasMessage = ("<b>Successfully disconnected from $myChildDevice.</b>")
            state.myStorageDevice = ''
            state.myStorageDeviceDNI = ''
            state.isStorageConnected = false
        }
        else {
            if (isLogInfo) log.info("connectTileStorageDevice(): No connection to $myChildDevice to disconnect.")
            state.hasMessage = "<b>No connection to $deviceDNI.</b>"
            state.isStorageConnected = false
        }
    }
    catch (ex) {
        log.warn("connectTileStorageDevice: Error disconnecting from $myChildDevice. Exception:$ex")
        state.hasMessage = "<b>Exception encountered. Error disconnecting from $myChildDevice. </b>"
        state.isStorageConnected = true
    }
}

//Delete a Remote Builder Storage Device.
def deleteTileStorageDevice() {
    if (isLogTrace) log.trace ('deleteTileStorageDevice: Entering deleteTileStorageDevice')
    myDeviceDNI = state.myStorageDeviceDNI
    state.hasMessage = "<b>Device Deletion initiated for $myDeviceDNI.</b>"
    if (isLogInfo) log.info("deleteTileStorageDevice: Initiating deletion of ${myDeviceDNI}.")
    deleteChildDevice("$myDeviceDNI")
    state.isStorageConnected = false
}

//Get a list of tiles from the device
def getTileList() {
    if (isLogTrace) log.trace ('getTileList: Entering getTileList')
    def tileList = []
    myDevice = getChildDevice(state.myStorageDeviceDNI)
    if (isLogDebug) log.debug("getTileList: myDevice: $myDevice")
    
    if (state.isStorageConnected == true) {    
        try { tileList = myDevice.getTileList() }
        catch (ex) {
            log.error("getTileList(): Failed - Error connecting to $selectedDevice. Exception:$ex")
            state.hasMessage = "<b>Exception encountered. Connection to '${selectedDevice}' failed.</b>"
            state.isStorageConnected = false
           }
        }
        return tileList
}

//Get a list of tiles from the device sorted by activity date.
def getTileListByActivity() {
    if (isLogTrace) log.trace ('getTileListbyActivity: Entering getTileListbyActivity')
    def tileList = []
    myDevice = getChildDevice(state.myStorageDeviceDNI)
    if (isLogDebug) log.debug("getTileList: myDevice: $myDevice")
    
    if (state.isStorageConnected == true) {
        try { tileList = myDevice.getTileListByActivity() }
        catch (ex) {
            log.error("getTileListByActivity(): Failed - Error connecting to $selectedDevice. Exception:$ex")
            state.hasMessage = "<b>Exception encountered. Connection to '${selectedDevice}' failed.</b>"
            state.isStorageConnected = false
           }
        return tileList
        }
}


//Delete a Remote Builder Tile on connected Storage Device.
def deleteTile() {
    if (isLogTrace) log.trace ('deleteTile: Entering deleteTile')
    myDeviceDNI = state.myStorageDeviceDNI
	def remoteNumber
	
	//Test to see if it is a valid tile selection
	if (tilesInUse == null || tilesInUse.size() < 5 ){
		log.info ("deleteTile: Invalid selection: Nothing done.")
		return
	}
	myArr = tilesInUse.tokenize(':')
	//log.debug ("myArr is: ${myArr}")
	selectedTile = myArr[0]
	//log.debug ("selectedTile is: ${selectedTile}")
	
	//Extract the Remote Number
	tileNumber = selectedTile.replace("Remote", "")
	tileNumber = tileNumber.replace("-Local", "")
	tileNumber = tileNumber.replace("-Cloud", "")
	//log.debug ("Tile Number is: ${tileNumber}")
	
	myDevice = getChildDevice(state.myStorageDeviceDNI)
	if (state.isStorageConnected == true) {
		log.info ("deleteTile: Delete tile initiated for tile number ${selectedTile} on device: ${myDeviceDNI}")
		myDevice.deleteTile(tileNumber)
	}
	
	//Remove the state entry used for remote links.
	state.remove("Remote" + tileNumber)
}

def checkLicense() {
	if ( state.activatedHubID.toString() == getHubUID().toString() ) return state.isAdvancedLicense
	else { return false	}
}


//*****************************************************************************************************
//Utility Functions
//*****************************************************************************************************

//Get the license type the user has selected.
def isAdvLicense(){
    if (isLogInfo) ("License:" + isAdvLicense)
    return isAdvLicense
}

//Functions to enhance text appearance
String bold(s) { return "<b>$s</b>" }
String italic(s) { return "<i>$s</i>" }
String underline(s) { return "<u>$s</u>" }
String dodgerBlue(s) { return '<font color = "DodgerBlue">' + s + '</font>' }
String myTitle(s1, s2) { return '<h3><b><font color = "DodgerBlue">' + s1 + '</font></h3>' + s2 + '</b>' }
String red(s) { return '<r style="color:red">' + s + '</r>' }
String green(s) { return '<g style="color:green">' + s + '</g>' }
String orange(s) { return '<g style="color:orange">' + s + '</g>' }

//Set the titles to a consistent style.
def titleise(title) {
    title = "<span style='color:#1962d7;text-align:left; margin-top:0em; font-size:20px'><b>${title}</b></span>"
}

//Set the titles to a consistent style without underline
def titleise2(title) {
    title = "<span style='color:#1962d7;text-align:left; margin-top:0em; font-size:20px'><b>${title}</b></span>"
}

//Set the notes to a consistent style.
String note(myTitle, myText) {
    return "<span style='color:#17202A;text-align:left; margin-top:0.25em; margin-bottom:0.25em ; font-size:16px'>" + '<b>' + myTitle + '</b>' + myText + '</span>'
}

//Set the body text to a consistent style.
String body(myBody) {
    return "<span style='color:#17202A;text-align:left; margin-top:0em; margin-bottom:0em ; font-size:18px'>"  + myBody + '</span>&nbsp'
}

//Produce a horizontal line of the speficied width
String line(myHeight) {
    return "<div style='background:#005A9C; height: " + myHeight + "px; margin-top:0em; margin-bottom:0em ; border: 0;'></div>"
}


String obfuscateStrings(String str1, String str2) {
    def result = ""
    int maxLength = Math.max(str1.length(), str2.length())

    for (int i = 0; i < maxLength; i++) {
        if (i < str1.length()) {
            result += str1.charAt(i)
        }
        if (i < str2.length()) {
            result += str2.charAt(i)
        }
    }
    return result
}

def activateLicense(){
    String hubUID = getHubUID()
    def P1 = (hubUID.substring(0, 8)).toUpperCase()
    def P2 = (hubUID.substring(Math.max(hubUID.length() - 8, 0))).toUpperCase()
    
    myResult = obfuscateStrings(P1.reverse().toString(), P2.reverse().toString())
    
    def firstHalf = myResult.substring(0, 8)
    def secondHalf = myResult.substring(8, 16)
    
    def key = firstHalf.toUpperCase() + "-" + secondHalf.toUpperCase()
    
    if (key == licenseKey) {
        state.isAdvancedLicense = true
		//Record the HUB ID for comparison in the event of a Hub change
		state.activatedHubID = hubUID
        return true
        }
    else return false
}

def getID(){
    //Create a Quasi Unique Identifier
    String hubUID = getHubUID()
    def P1 = (hubUID.substring(0, 8)).toUpperCase()
    def P2 = (hubUID.substring(Math.max(hubUID.length() - 8, 0))).toUpperCase()
    return ("${P1}-${P2}")
}


//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//**************
//**************  Button Related Functions
//**************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************

String buttonLink(String btnName, String linkText, int buttonNumber) {
    font = chooseButtonFont(buttonNumber)
    color = chooseButtonColor(buttonNumber)
    text = chooseButtonText(buttonNumber, linkText)
    return "<div class='form-group'><input type='hidden' name='${btnName}.type' value='button'></div><div><div class='submitOnChange' onclick='buttonClick(this)' style='color:${color};cursor:pointer;font-size:${font}px'>${text}</div></div><input type='hidden' name='settings[$btnName]' value=''>"
}

def chooseButtonColor(buttonNumber) {
    return (buttonNumber == settings.activeButton) ? '#00FF00' : '#000000'
}

def chooseButtonFont(buttonNumber) {
    return (buttonNumber == settings.activeButton) ? 20 : 15
}

def chooseButtonText(buttonNumber, buttonText) {
    return (buttonNumber == settings.activeButton) ? "<b>$buttonText</b>" : "<b>$buttonText</b>"
}


//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//**************
//**************  Installation and Update Functions
//**************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************
//************************************************************************************************************************************************************************************************************************

def installed() {
    if (isLogTrace) log.trace ('installed: Entering installed')
    if (isLogInfo) log.info "Installed with settings: ${settings}"
}

def updated() {
    if (isLogTrace) log.trace ('updated: Entering updated')
    if (isLogInfo) log.info "Updated with settings: ${settings}"
    unschedule()
    unsubscribe()
}

//Initialize all of the variables for a new instance or add variables for an update.
def initialize() {
    if (isLogTrace) log.trace("<b>initialize: Entering initialize</b>")

    if (state.initialized != true) {
        if (isLogTrace) log.trace("<b>initialize: Initializing all variables.</b>")

        // Set the flag so that this only ever runs once
        state.initialized = true

        // Logging
        app.updateSetting("isLogDebug", false)
        app.updateSetting("isLogTrace", false)
        app.updateSetting("isLogInfo", false)
        app.updateSetting("isLogWarn", true)
        app.updateSetting("isLogError", true)

        // UI State
        state.setupState = 0
        state.showIntro = true
        state.showLicense = false
        state.showDevice = false
        state.showCreateEdit = false
        state.showManage = false
        state.showMore = false
        state.descTextEnable = false
        state.debugOutput = false
        state.isStorageConnected = false
        state.flags = [selectedDeviceChanged: false]
        state.selectedDeviceHistory = [new: "seed1", old: "seed"]
        state.isAdvancedLicense = false
        state.activationState = "Not Activated"

        // Settings
        app.updateSetting("myInput", [value: "#c61010", type: "color"])
        app.updateSetting("selectedDevice", [value: "Remote Builder Storage Device 1", type: "text"])
    }
    
    // Version-gated updates
    if (state.variablesVersion == null || state.variablesVersion < codeVersion) {
        //Always update the SmartGrid base template on every upodate and update codeVersion
        state.smartGridTemplate = buildSmartGridTemplate()            
        state.variablesVersion = codeVersion
    }
}

//Determine if something has changed in the command list.
def isSelectedDeviceChanged() {
    if (state.selectedDeviceHistory.new != selectedDevice) {
        state.selectedDeviceHistory.old = state.selectedDeviceHistory.new
        state.selectedDeviceHistory.new = selectedDevice
        state.flags.selectedDeviceChanged = true
    }
    else { state.flags.selectedDeviceChanged = false }
}


//*******************************************************************************************************************************************************************************************
//**************
//**************  Remote Control APP-let Code
//**************
//*******************************************************************************************************************************************************************************************

//This contains the whole HTML\CSS\SVG\SCRIPT file equivalent. Placing it in a function makes it easy to collapse.
def buildSmartGridTemplate() {
    def now = new Date()
	state.smartGridTemplateTime = now.format("EEEE, MMMM d, yyyy '@' h:mm a")
	def HTML = '''
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>🪄#BrowserTitle#</title>
	//This is a placeholder for the link to the Google Materials Font. In its normal state it gets cleaned up with the comments because of the presence of the //.
	#MaterialsFont#
	
<style>
	:root {--control: #controlSize#px; 
			--tickMarks : repeating-linear-gradient(to right, transparent 0%, transparent 4%, black 5%, transparent 6%, transparent 9%);
			--blinds : repeating-linear-gradient(to right, black 0%, #ccc 3%, black 3%, #ccc 6%, black 6%, #ccc 9%);
			--shades : linear-gradient( 3deg, #000 0%, #333 45%, #CCC 55%, #FFF 100%);
			--dimmer : linear-gradient(to right, #000 0%, #333 15%, #666 30%, #888 45%, #AAA 60%, #DDD 75%, #FFF 100% ); 
			--CT : linear-gradient(to right, #FF4500 0%, #FFA500 16%, #FFD700 33%, #FFFACD 49%, #FFFFE0 60%, #F5F5F5 66%, #FFF 80%,	#ADD8E6 100% ); 
			}  

	html { display: flex; flex-direction: column; align-items: #ha#; height: 100%; margin: 0px; margin-top:#tmt#px; font-family: 'Arial', 'Helvetica', sans-serif; box-sizing: border-box; border:0px solid red;}
	body { display: flex; flex-direction: column; align-items: center; flex-grow: 1; overflow: hidden; height: 100%; cursor: auto; box-sizing: border-box; margin:0px;}
	//mark { #markTag# } ; m1 { #m1Tag# } ; m2 { #m2Tag# } ; 	m3 { #m3Tag# } ; m4 { #m4Tag# } ; m5 { #m5Tag# }
	mark { #markTag# } m1 { #m1Tag# display:inline-block; } m2 { #m2Tag# display:inline-block; } m3 { #m3Tag# display:inline-block; } m4 { #m4Tag# display:inline-block; } m5 { #m5Tag# display:inline-block; }
	.container {max-width: #maxWidth#px; align-items: center; width:100%; height: 100%; margin: 0px; overflow:auto; box-sizing: border-box; padding: #tpad#px;}
	
	/* Mobile Screens  */
	@media (min-resolution: 150dpi) and (hover:none) and (pointer: coarse) {
		.container {max-width:95%; width:100%; margin:#tmt#px auto; outline: 0px dotted red;} 
		html, body {justify-content: flex-start; align-items: stretch; height:auto; overflow:auto; } 
	}

	/* Eliminates scrollbars within the dashboard for Webkit browsers. For Firefox scrollbar-width: none; eliminates scrollbars within the Dashboard */
	//::-webkit-scrollbar {display: none;}

	/* Suppress the defaults for sliders on each browser platform */
	input[type=range] { -webkit-appearance: none; width: 100%; background: transparent; }
	
	/* Special styling for WebKit/Blink */
	input[type=range]::-webkit-slider-thumb { -webkit-appearance: none; height: var(--control); width: var(--control); border-radius: calc(var(--control) / 2); background: #1E90FF; cursor: pointer; vertical-align:middle;}
	
	/* Apply an outline when the slider is focused (clicked or selected) */
	input[type=range]:focus {outline: 2px solid #FF4500; }
	input[type=range]::-ms-track { width: 100%; cursor: pointer; background: transparent; border-color: transparent; color: transparent;}

	/* All the same stuff for Firefox and IE */
	input[type=range]::-moz-range-thumb, input[type=range]::-ms-thumb { height: 36px; width: 16px; border-radius: 3px; background: #1E90FF; cursor: pointer;}
	
	/* START OF TITLE CLASSES */
	.title{ padding: #tp#px; text-align: #ta#; font-size: #ts#%; font-weight: 400; color: #tc#; background-color: #tb#; display: #titleDisplay#;}
			
	/*  START OF TABLE CLASSES */
	table {width: calc(100%); border-collapse: collapse; table-layout: auto; border: #bwo#px solid #bc#; margin: 0 auto;}
	th, td { padding: #tvp#px #thp#px; text-align:center; vertical-align:middle; border: #bwi#px solid #bc#; transition:background-color 0.3s; user-select:none;}
	th { background-color: #hbc#; font-weight: bold; font-size: #hts#%; color: #htc#; margin:1px; }
	
	.ascSort { border-bottom: #bwi#px solid #sortHeaderHintAZ#;}
	.descSort { border-bottom: #bwi#px solid #sortHeaderHintZA#;}

	tr { background-color: #rbc#; color: #rtc#; font-size:#rts#%;}
	tr:hover { background-color: #rbs#;} 
    
	.selected-row {	background-color: #rbs#;}
	.custom-row-group {background-color: #crbc#; color:#crtc#; font-size: #crts#%;}
	//Drag and Drop Support
	#isDragDropCSS#

	/* Widths of columns 1, 2 & 10 are derived from the width of the control element. Columns 5 and 6 are fixed as set by the user. The remaining columns with be auto-calculated to best fit the text. */
	th:nth-child(1), td:nth-child(1) { width:calc(var(--control) * 1.5); display:#hideColumn1#; }
	th:nth-child(2), td:nth-child(2) { width:calc(var(--control) * 2.5); display:#hideColumn2#; }
	th:nth-child(3) {padding-left:calc(#thp#px + 5px);}
	td:nth-child(3) {text-align:left; padding-left:calc(#thp#px); }
	th:nth-child(4), td:nth-child(4) { width:calc(var(--control) * 3); display:#hideColumn4#; }
	th:nth-child(5), td:nth-child(5) { width:#column5Width#px; display:#hideColumn5#; }
	th:nth-child(6), td:nth-child(6) { width:#column6Width#px; display:#hideColumn6#; }
	th:nth-child(7), td:nth-child(7) { display:#hideColumn7#; }
	th:nth-child(8), td:nth-child(8) { display:#hideColumn8#; }
	th:nth-child(9), td:nth-child(9) { display:#hideColumn9#; }
	th:nth-child(10), td:nth-child(10) { width:calc(var(--control) * 2); display:#hideColumn10#; }
	th:nth-child(11), td:nth-child(11) { display:#hideColumn11#; }
	th:nth-child(12), td:nth-child(12) { display:#hideColumn12#; }
	
	/* START OF CONTROLS CLASSES */			
	/* Column 1 Checkboxes */
	input[type="checkbox"] {height:var(--control); width:var(--control); margin:0px; margin-top:3px; cursor: pointer; }

	/* Column 2 - Materials Symbols - Icons */
	.material-symbols-outlined {padding:3px; border-radius: 50%; font-size:calc(var(--control) * 1.5 )}
	.material-symbols-outlined.on { background-color:rgba(255,255,0, 0.3); color: #333333;}
	.material-symbols-outlined.off {color: #000000; opacity:0.5;}
	.material-symbols-outlined.group {color: #000000; opacity:0.8;}
	.open { background-color:rgba(255,213,128, 0.5); color:#333333;}
	.good { background-color:rgba(0,255,0, 0.5); color:#333333;}
	.warn { background-color:rgba(255,140,0, 0.5); color:#333333;}
	.bad { background-color:rgba(255,0,0, 0.5); color:#333333;}
			
	/* Column 4 On/Off Switch */
	.toggle-switch { position: relative; display: inline-block; vertical-align: middle; margin-top: calc(var(--control) / 3); margin-bottom: calc(var(--control) / 5 ); width: calc(var(--control) * 2); height: var(--control); background-color: #CCC; cursor: pointer; border-radius: calc(var(--control) / 2); transition: background-color 0.3s ease; box-shadow: 0 0 calc(var(--control) / 1.5) 0px rgba(255, 99, 71, 1); }
	.toggle-switch::before { content: ''; position: absolute; width: calc(var(--control) * 0.87); height: calc(var(--control) * 0.87); border-radius: 50%; background-color: white; top: calc(var(--control) * 0.066); left: calc(var(--control) * 0.066); transition: transform 0.3s ease; }
	.toggle-switch.on { background-color: #2196F3; box-shadow: 0 0 calc(var(--control) / 1.5) calc(var(--control) / 5) rgba(255, 255, 0, 1); }
	.toggle-switch.on::before { transform: translateX(calc(var(--control))); }

	/* Column 5 - Control Group 1 - Level and Kelvin Sliders */
	.control-container {display:flex;position: relative; width: 95%; display: flex; justify-content: center; align-items: center; background-color:#rbc#; margin:auto; }
	.CT-slider, .level-slider, .blinds-slider, .shades-slider, .volume-slider, .tilt-slider { width: 90%; opacity:0.75; border-radius:0px; height:var(--control); outline: 2px solid #888; cursor: pointer;}
	.CT-value, .level-value, .blinds-value, .shades-value, .volume-value, .tilt-value, .state-value {position:absolute; top:50%; transform:translateY(-50%); font-size:#rts#%; pointer-events:none; text-align:center; cursor:pointer; font-weight:bold; background:#fff8; padding:0px;color: #rtc#;}
	.state-text {font-size:#rts#%; pointer-events:none; cursor:pointer;}

	/* Custom properties for WebKit-based browsers (Chrome, Safari) */
	.CT-slider::-webkit-slider-runnable-track { background: var(--CT); height: 100% }
	.level-slider::-webkit-slider-runnable-track { background: var(--dimmer); height: 100%}	
	.blinds-slider::-webkit-slider-runnable-track { background: var(--blinds); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.shades-slider::-webkit-slider-runnable-track { background: var(--shades); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }

	//This creates a series of tick marks representing 0 - 100 with a color gradient background from green to red as volume increases.
	.volume-slider::-webkit-slider-runnable-track { background: var(--tickMarks), linear-gradient(to right, green, orange, red); height: 100%; background-size: 100% 100%; background-repeat: no-repeat; border: 0px solid #DDD;}
	.tilt-slider::-webkit-slider-runnable-track { background: var(--tickMarks),linear-gradient(to right, #000 0%, #333 10%, #0D47A1 30%, #17D 40%, #4682B4 50%, #8CF 65%, #FFF 85%, #FFF 100%); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.level-slider::-webkit-slider-thumb, .CT-slider::-webkit-slider-thumb, .shades-slider::-webkit-slider-thumb, .blinds-slider::-webkit-slider-thumb, .volume-slider::-webkit-slider-thumb { background: dodgerblue; border-radius: 50%; cursor: pointer; }   
	.tilt-indicator {font-size: var(--control); line-height: var(--control); transform-origin: center center; transition: transform 0.1s ease-in-out;}
	
	/* Add Mozilla Browser Support - Untested */
	.CT-slider::-moz-range-track { background: var(--CT); height: 100%; }
	.level-slider::-moz-range-track { background: var(--dimmer); height: 100%; }   
	.blinds-slider::-moz-range-track { background: var(--blinds); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.shades-slider::-moz-range-track { background: var(--shades); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.volume-slider::-moz-range-track { background: var(--tickMarks), linear-gradient(to right, green, orange, red); height: 100%; background-size: 100% 100%; background-repeat: no-repeat; border: 0px solid #DDD; }
	.tilt-slider::-moz-range-track { background: var(--tickMarks), linear-gradient(to right, #000 0%, #333 10%, #0D47A1 30%, #17D 40%, #4682B4 50%, #8CF 65%, #FFF 85%, #FFF 100%); height: 100%; background-size: 100% 100%, 100% 100%; border: 0px solid #DDD; }
	.level-slider::-moz-range-thumb, .CT-slider::-moz-range-thumb, .shades-slider::-moz-range-thumb, .blinds-slider::-moz-range-thumb, .volume-slider::-moz-range-thumb { background: dodgerblue; border-radius: 50%; cursor: pointer;}
	
	/* Column 6 - Control Group 2 - Color */
	input[type="color"]::-webkit-color-swatch-wrapper { padding: 0px; }
	.colorPicker{border: 2px solid #888; border-radius: 2px; width: calc(var(--control) * 3); height: calc( var(--control) * 1.25); cursor: pointer;}	/* Column 6 Color Control */

	/* Info Columns 7 & 8 & 9 */
	.info1, .info2, .info3 { border:none; background:transparent; white-space:nowrap; color:#rtc#;}
	.info1 { font-size: #its1#%; text-align: #ita1#; }
	.info2 { font-size: #its2#%; text-align: #ita2#; }
	.info3 { font-size: #its3#%; text-align: #ita3#; }

	/* Define glow effects */	
	.glow-EffectSuccess {border-color: #pollUpdateColorSuccess#;}
	.glow-EffectFail {border-color: #pollUpdateColorFail#;}
	.glow-EffectPending {border-color: #pollUpdateColorPending#;}
	.glow-EffectCT {outline: 2px solid #1E90FF;}
	.glow-EffectRGB {border: 2px solid #1E90FF;}
		
	/* Refresh bar styling */
	#shuttle { display:none; position:relative; height:#shuttleHeight#px; width:5%; background-color:#shuttleColor#; border-radius:3px; animation:none; top: -#shuttleHeight#px;}
	@keyframes slide {0% { transform: translateX(0%); } 50%  { transform: translateX(1900%); } 100% { transform: translateX(0%); } }
	@keyframes blink { 0% { opacity: 1; } 50% {opacity: 0;} 100% {opacity: 1;} }
	.blinking { animation: blink 1s infinite; }
	.blinking_orange { animation: blink 1s infinite; background-color: rgba(255, 140, 0, 0.7); }

	.button-group { display: flex; justify-content: space-between; align-items: center; margin: 0 auto; text-align: center;}
	.button { flex:1;  max-height: var(--control); padding: 4px 8px; margin: 0 2px; border-radius: var(--control); background-color: #555555; text-align: center; transition: background-color 0.3s, border-color 0.3s, outline 0.3s; 
			display: flex; align-items: center; justify-content: center; color: #FFF; font-size: calc(2 + var(--control) / 1.5); cursor: pointer; }
	.button:hover { background-color: #3CB371;}
	.button:active { background-color: #1C86EE;}
	.button.selected { background-color: #1E90FF;}
	.button-group.disabled { opacity: 0.75; pointer-events: none; cursor: not-allowed; }

	@keyframes spin { from {transform: rotate(0deg);} to {transform: rotate(360deg);} }
	.spin-low {animation: spin 3s linear infinite;}
	.spin-medium {animation: spin 1.5s linear infinite;}
	.spin-high {animation: spin 0.75s linear infinite;}

    .modal {display:none; position:fixed; z-index:1000; left:0; top:0; width:100%;height:100%; background-color:rgba(0,0,0,0.5);overflow: auto;}
	.modal-content {background-color:white; margin:20px auto; padding:20px; border-radius:8px; width:300px; text-align:left;overflow-y: auto;}
	.modal-content select, .modal-content input { margin-bottom: 10px; font-size: 16px; }
    .close {cursor:pointer; float:right; font-size: 24px}
	.group-icon-active { transform: rotate(0deg); transition: transform 0.3s ease;}
	.group-icon-inactive { transform: rotate(90deg); background-color: #F0F0F0; border-radius: 50%; padding: 2px; transition: transform 0.5s ease, background-color 0.5s ease; }

	iframe {position: relative; z-index: 10; pointer-events: auto;}
}

</style>
</head>

//***********************************************  HTML Block  *************************************************************************
//**************************************************************************************************************************************

<body>
<div class="container">
	<div class="title" id="title">#tt#</div>
	<table id="sortableTable">
		<thead>
			<tr>
				<th><input type="checkbox" id="masterCheckbox" onclick="toggleAllCheckboxes(this)" title="Select All/Deselect All"></th>
				<th id="icon" class="sortLinks" onclick="sortTable(1, this);" title="Icon - Sort"><span id="iconHeader">Icon</span></th>
				<th id="nameHeader" class="sortLinks" onclick="sortTable(2, this);"> <div style="display: flex; justify-content: space-between; align-items: center;">
	        		<span title="Sort Name A-Z">#column3Header#</span><span id="refreshIcon" style="font-size: 1.5em; cursor:pointer;" onclick="event.stopPropagation(); refreshPage(50);" 
					title="Refresh Data"><b>↻</b></span></div></th>
				<th id="stateHeader" class="sortLinks th" onclick="sortTable(3, this);"><span title="Sort State On-Off">State</span></th>
				<th id="ControlAB" class="sortLinks" onclick="toggleControl()" title="Toggle Control A/B">#column5Header#</th>
				<th id="color">#column6Header#</th> 
				<th id="i1" class="sortLinks" onclick="sortTable(6, this);" title="Info1 - Alpha Sort"><span id="info1Header">#Info1#</span></th>
				<th id="i2" class="sortLinks" onclick="sortTable(7, this);" title="Info2 - Alpha Sort"><span id="info2Header">#Info2#</span></th>
				<th id="i3" class="sortLinks" onclick="sortTable(8, this);" title="Info3 - Alpha Sort"><span id="info3Header">#Info3#</span></th>
				<th id="Unused" title="Unused"><span id="unusedHeader">Unused</span></th>
				<th id="sort">Custom Sort</th>
				<th id="group">Group</th>
			</tr>
		</thead>
		<tbody><!-- Table rows will be dynamically loaded from JSON --></tbody>
	</table>
	<div id="shuttle"></div>
</div>

<!-- Settings Window -->
<div id="myModal" class="modal">
    <div class="modal-content">
        <h2 style="text-align: center;"><u>Settings</u></h2>
        <label for="filterSwitch"><strong>Display Switches:</strong></label>
        <select id="filterSwitch"><option value="allSwitch" selected>All Switches</option><option value="onlyOn">Only On</option></select><br>
		<label for="filterContact"><strong>Display Contacts:</strong></label>
        <select id="filterContact"><option value="allContact" selected>All Contacts</option><option value="onlyOpen">Only Open</option></select><br>
		<label for="filterLeak"><strong>Display Leak Sensors:</strong></label>
        <select id="filterLeak"><option value="allLeak" selected>All Sensors</option><option value="onlyWet">Only Wet</option></select><br>
		<label for="filterLock"><strong>Display Locks:</strong></label>
        <select id="filterLock"><option value="allLock" selected>All Locks</option><option value="onlyUnlocked">Only Unlocked</option></select><br>
		<label for="filterMotion"><strong>Display Motion:</strong></label>
        <select id="filterMotion"><option value="allMotion" selected>All Motion</option><option value="onlyActive">Only Active</option></select><br>
        <label for="isPolling"><strong>Polling Enabled:</strong></label>
		<select id="isPolling"><option value="true">true</option><option value="false">false</option></select><br>
		<label for="pollInterval"><strong>Poll Interval(ms):</strong></label>
		<input type="number" id="pollInterval" value="5000" min="1000" max="60000" step="1000" /><br>
		<label for="isLogging"><strong>Logging Enabled:</strong></label>
		<select id="isLogging"><option value="true">true</option><option value="false">false</option></select><br><br>
		<button id="modalCloseBtn" style="display: block; margin: 0 auto; width: 50%; padding: 10px; font-size: 1rem; text-align: center;"><b>Close</b></button>
    </div>
</div>

<script>
//alert ("Updated V1");

// Global variables to track resources that need cleanup
let pollingInterval;
let pressTimer;					// Used to determine when to pop up the modal screen
let currentFetchController;		//Used for the fetch operation
let pollingTimeoutID = null;	//Handle for the Polling Timeout
const groupRow = 51;
const deviceRow = 52;
const iFrameRow = 53;
const valve = 10;
const stateMap = #deviceStateMap#;
const intraGroupSortState = {};

// Ensure each iframe has a unique window.name to scope storage keys
// The iFrame preview in the composer has a window.name of "AppID" followed by a "-P" (Preview) so it looks like "4912-P".  An Applet loaded elsewhere will have have either a "-A" or "-B" suffix depending on which link is being used.
if (!window.name) { window.name = 'applet_' + Math.random().toString(36).substr(2, 5); }

// Generate a scoped key using window.name instead of AppID
const storageKey = (key) => `${window.name}_${key}`;

// Modal constants
const modal = document.getElementById("myModal");

// LocalStorage operations (persistent per iframe)
let storedSortDirection = JSON.parse(localStorage.getItem(storageKey("sortDirection")));
let sortDirection = storedSortDirection || { activeColumn: 2, direction: 'asc' };
let showSlider = ( localStorage.getItem(storageKey("showSlider")) === "A" || localStorage.getItem(storageKey("showSlider")) === "B" ) ? localStorage.getItem(storageKey("showSlider")) : "A";
let lastCommand = "none";
let isSliding = false;
let pollingActive = true;
let isWindowActive = true;

// Use localStorage (instead of sessionStorage) for all settings
let sessionID = localStorage.getItem(storageKey("sessionID"));
if (!sessionID) { 
	sessionID = (Math.abs(Math.random() * 0x7FFFFFFF | 0)).toString(16).padStart(8, '0');
	localStorage.setItem(storageKey("sessionID"), sessionID);
	}

if (!localStorage.getItem(storageKey("Collapsed_Groups"))) { localStorage.setItem(storageKey("Collapsed_Groups"), JSON.stringify([])); }
const COLLAPSED_GROUPS_KEY = storageKey("Collapsed_Groups");

let pollInterval = localStorage.getItem(storageKey("pollInterval")) || "#pollInterval#";
localStorage.setItem(storageKey("pollInterval"), pollInterval);

// Initialize isPolling as a Boolean
if (!localStorage.getItem(storageKey("isPolling"))) { localStorage.setItem(storageKey("isPolling"), "#isPolling#"); }
let isPolling = localStorage.getItem(storageKey("isPolling")) === "true";

// Initialize Logging as a Boolean
if (!localStorage.getItem(storageKey("isLogging"))) { localStorage.setItem(storageKey("isLogging"), "false"); }
let isLogging = localStorage.getItem(storageKey("isLogging")) === "true";

let isDragDrop = #isDragDrop#;
let isCustomSort = #isCustomSort#;
const shuttle = document.getElementById('shuttle');

//Disable Polling if we are using Drag and Drop
if ( isDragDrop ) isPolling = false;

//These are all the filterValues used in the Modal window
const filterValues = {};
["filterSwitch", "filterContact", "filterLeak", "filterLock", "filterMotion"].forEach(id => {
  const key = storageKey(id);
  let value = localStorage.getItem(key);
  if (!value) {
    const defaultVal=id === "filterSwitch" ? "allSwitch" : id === "filterContact" ? "allContact" : id === "filterLeak" ? "allLeak" : id === "filterLock" ? "allLock" : id ==="filterMotion" ? "allMotion" : "";
    localStorage.setItem(key, defaultVal);
    value = defaultVal;
  }
  document.getElementById(id).value = value;
  filterValues[id] = value;   // Store current values if needed later
  document.getElementById(id).addEventListener("change", function () {
    localStorage.setItem(key, this.value);
  });
});

document.getElementById("isPolling").addEventListener("change", function() { localStorage.setItem(storageKey("isPolling"), this.value);});
document.getElementById("pollInterval").addEventListener("change", function() { localStorage.setItem(storageKey("pollInterval"), this.value);});
document.getElementById("isLogging").addEventListener("change", function() { localStorage.setItem(storageKey("isLogging"), this.value);});

//Output some logging Information if it is enabled
console.log ("isLogging", isLogging);
if (isLogging) console.log("window.name:", window.name);
if (isLogging) console.log("storageKey:", storageKey);
if (isLogging) console.log ("isDragDrop", isDragDrop);
if (isLogging) console.log ("isPolling", isPolling);
if (isLogging === 'true'){ showVariables(); }

//Polling Control
if ( isPolling ) { 
	const poller = startPolling('#URL1#', pollResult); 
	//Start the Shuttle animation
	const shuttle = document.getElementById('shuttle');
	shuttle.style.display = 'block';
	shuttle.style.animation = `slide ${pollInterval *2}ms ease-in-out infinite`;
}

//This has to do with transaction handling
let transactionTimeout = #commandTimeout#;
let transaction = null;

// Setup event listeners for the name header with proper cleanup
const nameHeader=document.getElementById("nameHeader");
function setupHeaderEventListeners() {
  // Remove existing listeners first to prevent duplicates
  removeHeaderEventListeners();
  
  // Add new listeners
  nameHeader.addEventListener("mousedown", startPress, { passive:true });
  nameHeader.addEventListener("touchstart", startPress, { passive:true });
  ["mouseup", "touchend", "dragstart", "dragend"].forEach(e => nameHeader.addEventListener(e, cancelPress));
}

function removeHeaderEventListeners() {
  nameHeader.removeEventListener("mousedown", startPress);
  nameHeader.removeEventListener("touchstart", startPress);
  ["mouseup", "touchend", "dragstart", "dragend"].forEach(e => nameHeader.removeEventListener(e, cancelPress));
}

// Initialize event listeners
setupHeaderEventListeners();

// Modal control functions
function openModal() { modal.style.display = "block"; document.getElementById("isPolling").value = isPolling; document.getElementById("isLogging").value = isLogging; document.getElementById("pollInterval").value = pollInterval;}
function closeModal() { modal.style.display = "none"; refreshPage(50);}
function startPress(event) { if (pressTimer) clearTimeout(pressTimer); pressTimer=setTimeout(openModal, 1999); }
function cancelPress() { if (pressTimer) { clearTimeout(pressTimer); pressTimer = null; } }
document.getElementById("modalCloseBtn").addEventListener("click", closeModal);


//***********************************************  Table Body  *************************************************************************
//**************************************************************************************************************************************

function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function showVariables(){
	console.log("Session Storage Variables:");
	for (let i = 0; i < sessionStorage.length; i++) {
	    let key = sessionStorage.key(i);
	    console.log(`${key}: ${sessionStorage.getItem(key)}`);
	}
}

function loadTableFromJSON(data) {
    const fragment = document.createDocumentFragment();
    const tbody = document.querySelector("#sortableTable tbody");
    let icon = "";
    tbody.innerHTML = "";
    const saved = JSON.parse(sessionStorage.getItem(storageKey("checkboxStates"))) || {};

    data.forEach((d, i) => {
        let show = true;
        if (filterValues["filterSwitch"] === "onlyOn" && d.type >= 1 && d.type <= 5 && d.switch !== "on") show = false;
        if (filterValues["filterLock"] === "onlyUnlocked" && d.type === 11 && d.switch !== "off") show = false;
        if (filterValues["filterContact"] === "onlyOpen" && d.type === 31 && d.switch !== "open") show = false;
        if (filterValues["filterLeak"] === "onlyWet" && d.type === 33 && d.switch !== "wet") show = false;
        if (filterValues["filterMotion"] === "onlyActive" && d.type === 34 && d.switch !== "active") show = false;
        if (!show) return;
        const row = document.createElement('tr');
        row.draggable = #isDragDrop#;
        Object.assign(row.dataset, {
            ID: d.ID, name: d.name, type: d.type, speed: d.speed, level: d.level,
            position: d.position, tilt: d.tilt, volume: d.volume, colorMode: d.colorMode || "None",
            info1: d.i1, info2: d.i2, info3: d.i3, icon: d.icon, class: d.cl, row: d.row, group: d.group
        });

        const col = /^#[0-9A-F]{6}$/i.test(d.color) ? d.color : "#FFF";
        if (d.type === groupRow) icon = `<i class='material-symbols-outlined ${d.cl}' onclick="toggleGroupVisibility(this)">${d.icon}</i>`;
        else icon = `<i class='material-symbols-outlined ${d.cl}'>${d.icon}</i>`;

        const getSlider = (cls, val, min, max, label, disp, extra = "") => `
            <input type="range" class="${cls}" min="${min}" max="${max}" value="${val}"
                style="display:${disp}" oninput="updateSliderValue(this, '${label}')" onchange="updateHUB()" ${extra}>
            <span class="${label}-value" style="display:${disp}">${val}${label === "CT" ? "°K" : "%"}</span>`;
        let c1 = "", c2 = "";
        if (d.type <= 5) {
            c1 += `<div class="control-container">`;
            if ([2,3,4,5].includes(d.type)) c1 += getSlider("level-slider", d.level, 0, 100, "level", showSlider === 'A' ? 'block' : 'none');
            if ([3,5].includes(d.type)) c1 += getSlider(`CT-slider ${d.colorMode === 'CT' ? 'glow-EffectCT' : ''}`, d.CT, 2000, 6500, "CT", showSlider === 'B' ? 'block' : 'none');
            c1 += `</div>`;
        }
        if (d.type === 12) c1 = `
            <div class="button-group ${d.switch === 'off' ? 'disabled' : ''}">
                ${["low","medium","high"].map(s => `<div class="button ${d.speed === s ? 'selected' : ''} ${d.switch === 'off' ? 'disabled' : ''}" data-speed="${s}" onclick="speed(this); updateHUB(); toggleChecked(this)">${s[0].toUpperCase()}</div>`).join("")}
            </div>`;
        if ([14,15].includes(d.type)) c1 = `<div class="control-container"><input type="range" class="shades-slider" min="0" max="100" value="${d.position}" oninput="updateSliderValue(this, 'position')" onchange="updateHUB()"><span class="shades-value"><b>${d.position}%</b></span></div>`;
        if (d.type === 15) c2 = `<div style="display:flex;align-items:center;"><div class="control-container"><input type="range" class="tilt-slider" min="0" max="90" value="${d.tilt}" oninput="updateSliderValue(this,'tilt')" onchange="updateHUB()"><span class="tilt-value">${d.tilt}°</span></div><div id="tilt-indicator" class="tilt-indicator" style="margin-left:20px;margin-right:10px;">|</div></div>`;
        if (d.type === 16) c1 = `<div class="control-container"><input type="range" class="volume-slider" min="0" max="100" value="${d.volume}" oninput="updateSliderValue(this, 'volume')" onchange="updateHUB()"><span class="volume-value">${d.volume}%</span></div>`;
        if ([4,5].includes(d.type)) c2 = `<input type="color" class="colorPicker ${d.colorMode === 'RGB' ? 'glow-EffectRGB' : ''}" id="colorInput${i}" value="${col}" onchange="updateColor(this); updateHUB()">`;
        const state = d.type <= 30
            ? `<div class="toggle-switch ${d.switch === 'on' ? 'on' : ''}" data-state="${d.switch}" onclick="toggleSwitch(this); updateHUB()"></div>`
            : `<div class="state-text">${d.switch}</div>`;
        if (d.type === groupRow || d.type === deviceRow) {c1 = d.level; c2 = d.CT};
        if ([1,10,11,13].includes(d.type)) c1 = "";
        if (d.type === 53) {
            row.innerHTML = `<td colspan="1"></td><td colspan="1">${icon}</td><td colspan="#iFrameColspan#">${state}</td><td style="display:#hideColumn11#;">${d.row}</td><td style="display:#hideColumn12#;">${d.group}</td>`;
        } else {
            row.innerHTML = `
                <td><input type="checkbox" class="option-checkbox" ${saved[d.ID] ? "checked" : ""} onchange="toggleRowSelection(this)"></td>
                <td>${icon}</td>
                <td ${Number(d.type) === groupRow ? `onclick="intraGroupNumericSort(document.querySelector('#sortableTable tbody'), this.closest('tr').dataset.group)" style="cursor:pointer;"` : ''}>${d.name}</td>
                <td>${state}</td><td>${c1}</td><td>${c2}</td>
                <td><div class="info1">${d.i1}</div></td><td><div class="info2">${d.i2}</div></td><td><div class="info3">${d.i3}</div></td>
                <td></td><td>${d.row}</td><td>${d.group}</td>`;
        }
        fragment.appendChild(row);
    });

    tbody.appendChild(fragment);
    if (isDragDrop) {
        tbody.querySelectorAll("tr").forEach(r => {
            r.addEventListener("dragstart", e => e.target.classList.add("dragging"));
            r.addEventListener("dragend", e => { e.target.classList.remove("dragging"); saveRowOrder(); });
        });
        tbody.addEventListener("dragover", e => {
            e.preventDefault();
            const d = document.querySelector(".dragging");
            const a = [...tbody.querySelectorAll("tr:not(.dragging)")].reduce((c, el) => {
                const o = e.clientY - el.getBoundingClientRect().top - el.offsetHeight / 2;
                return o < 0 && o > c.offset ? { offset: o, el } : c;
            }, { offset: -Infinity }).el;
            tbody.insertBefore(d, a ?? null);
        });
    }
    if (isCustomSort) { sortTable(10); setColumnHeaders(true); }
    else setColumnHeaders(false);
    if (isCustomSort) assignGroupNumbers();
    restoreCollapsedGroups();
    updateAllTiltIndicators();
    updateRows();
    updateState();
    colorizeNumericSensors([
        { type: 32, highThreshold: #maxTemp#, lowThreshold: #minTemp#, highColor: "#maxTempColor#", lowColor: "#minTempColor#", midColor: "#normalTempColor#" },
        { type: 38, highThreshold: #maxHumidity#, lowThreshold: #minHumidity#, highColor: "#maxHumidityColor#", lowColor: "#minHumidityColor#", midColor: "#normalHumidityColor#" }
    ]);
}

//******************************  Custom Rows, Manual Sort Order and Collapsible Groups ************************************************
//**************************************************************************************************************************************

//Updates the value of the State field to display a different text, language or formatting.
function updateState() {
    document.querySelectorAll("#sortableTable tr").forEach(row => {
        const stateCell = row.cells[3]; // Adjust this index if necessary
        if (!stateCell) return;

        const rawState = stateCell.textContent.trim().toLowerCase();
        if (stateMap?.hasOwnProperty(rawState)) {
            stateCell.innerHTML = stateMap[rawState];  // ← This allows HTML formatting
        }
    });
}

//Formats the appearance of the Custom Rows. 51: Group Row, 52: Device Row, 53: iFrame Row  
function updateRows() {
    document.querySelectorAll("#sortableTable tr").forEach(row => {
        if (Number(row.dataset.type) === groupRow) {
            row.style.background = "linear-gradient(to bottom, #crbc#, #crbc2#)";
            [...row.cells].forEach((cell, i) => {
                if (!cell) return;
                if ([1, 2, 3].includes(i)) cell.classList.add(".custom-row-group");
                if (i === 0) cell.innerHTML = "";
                cell.style.borderRight = "2px solid transparent";
            });
        }
		//Remove the Selection box for anything of type >= 30, namely sensors or custom rows.
		if (+row.dataset.type >= 30) {
            const cell = row.cells[0];
            if (cell) cell.innerHTML = "";
        }
    });
}

//Save the sort order after they are manually sorted.
function saveRowOrder() {
    const rows = [...document.querySelectorAll("#sortableTable tbody tr")];
    const sortedJSON = rows.map((row, index) => {
        const baseID = row.dataset.ID;
        const UID = row.dataset.ID + "-" + row.dataset.type;
        return { row: index + 1, ID: baseID, UID: UID };
    });
    sessionStorage.setItem(storageKey("customSortOrder"), JSON.stringify(sortedJSON));
    const output = { customSortOrder: sortedJSON };
    const myData = JSON.stringify(output);
    sendData(myData);
}

//Assigns a Group number to each row so we can Hide all members of the group with the same Group number.
function assignGroupNumbers() {
	const rows = document.querySelectorAll("#sortableTable tbody tr");
	let group = 0;
	rows.forEach(row => {
		const typeStr = row.dataset.type;
		const type = parseInt(typeStr);
		//console.log(`Row type: ${typeStr} parsed as ${type}, current group: ${group}`);
		if (type === groupRow) { group++; }
		row.dataset.group = group;
		const groupCell = row.querySelector("td:last-child");
		if (groupCell) groupCell.textContent = group;
	});
}


//***********************************************  Collapsible Groups  *****************************************************************
//**************************************************************************************************************************************

//Add a collapsed group to the local settings
function addCollapsedGroup(groupNum) {
  const list = JSON.parse(localStorage.getItem(COLLAPSED_GROUPS_KEY) || "[]");
  if (!list.includes(groupNum)) {
    list.push(groupNum);
    localStorage.setItem(COLLAPSED_GROUPS_KEY, JSON.stringify(list));
  }
}

//Remove a collapsed group to the local settings
function removeCollapsedGroup(groupNum) {
	let list = JSON.parse(localStorage.getItem(COLLAPSED_GROUPS_KEY) || "[]");
	list = list.filter(n => n !== groupNum);
	localStorage.setItem(COLLAPSED_GROUPS_KEY, JSON.stringify(list));
}

function setGroupCollapsedState(groupNum, collapsed) {
    const rowsInGroup = [...document.querySelectorAll(`#sortableTable tr[data-group="${groupNum}"]`)];
    if (rowsInGroup.length === 0) return;

    // Find the group row (the one with type === groupRow)
    const iconRow = rowsInGroup.find(r => Number(r.dataset.type) === groupRow);
    if (!iconRow) return;

    const icon = iconRow.querySelector("i.material-symbols-outlined");
    if (!icon) return;
	
	// Hide all rows except iconRow when collapsed; else show all
    rowsInGroup.forEach(row => { row.style.display = (collapsed && row !== iconRow) ? "none" : ""; });

    // Icon class toggle: active means expanded (not collapsed)
    icon.classList.toggle("group-icon-active", !collapsed);
    icon.classList.toggle("group-icon-inactive", collapsed);
}

//Collapses or Expands the Groups.
function toggleGroupVisibility(iconElement) {
    const row = iconElement.closest("tr");
    if (!row) return;

    const group = row.dataset.group;
    if (!group) return;

    const rowsInGroup = [...document.querySelectorAll(`#sortableTable tr[data-group="${group}"]`)];
    const otherRows = rowsInGroup.filter(r => r !== row);

    // Determine if currently collapsed by checking if all other rows are hidden
    const currentlyCollapsed = otherRows.every(r => r.style.display === "none");
    const newCollapsedState = !currentlyCollapsed;

    setGroupCollapsedState(Number(group), newCollapsedState);

    if (newCollapsedState) { addCollapsedGroup(Number(group));} 
    else { removeCollapsedGroup(Number(group)); } 
}

//Restores the settings for Collapsed Groups after a refresh
function restoreCollapsedGroups() {
    const collapsedGroups = JSON.parse(localStorage.getItem(COLLAPSED_GROUPS_KEY) || "[]");
    collapsedGroups.forEach(groupNum => setGroupCollapsedState(groupNum, true));
}


//***********************************************  Data Transfer ***********************************************************************
//**************************************************************************************************************************************

//Update the Tilt angle on ALL tilt indicators
function updateAllTiltIndicators() {
    // Select all rows in the table then iterate through each row
    const rows = document.querySelectorAll("#sortableTable tbody tr");
    rows.forEach((row, index) => {
        // Find the tilt slider and tilt indicator in the row
        const tiltSlider = row.querySelector(".tilt-slider");
        const tiltIndicator = row.querySelector(".tilt-indicator");
        if (!tiltSlider  || !tiltIndicator) return;
	    tiltIndicator.style.transform = `rotate(${-tiltSlider.value}deg)`; // Counterclockwise rotation
    });
}

function updateHUB() {
	//Update the table border to show activity is pending if the lastCommand is something other than "none".
	document.querySelector("table").classList.add('glow-EffectPending');
	const output = [...document.querySelectorAll("#sortableTable tbody tr")].map(row => {
		const { type, ID } = row.dataset;
		const t = Number(type);
		if (t > 30) return null;	//Don't return anything if it is a sensor or custom device.

		const o = { ID, type };
		const $ = s => row.querySelector(s);
		const add = (cond, key, valFn) => cond && (o[key] = valFn());

		add(t >= 1 && lastCommand === "switch", "switch", () => $(".toggle-switch")?.dataset.state);
		add(t >= 2 && t <= 5 && lastCommand === "level", "level", () => +$(".level-slider")?.value || 0);
		add((t === 3 || t === 5) && lastCommand === "CT", "CT", () => +$(".CT-slider")?.value || 0);
		add((t === 4 || t === 5) && lastCommand === "color", "color", () => ($('input[type="color"]')?.value || "#000000").toUpperCase());
		add(t === 12 && lastCommand === "speed", "speed", () => $(".button.selected")?.dataset.speed || "off");
		add((t === 14 || t === 15) && lastCommand === "position", "position", () => +$(".shades-slider")?.value || 0);
		add(t === 15 && lastCommand === "tilt", "tilt", () => +$(".tilt-slider")?.value || 0);
		add(t === 16 && lastCommand === "volume", "volume", () => +$(".volume-slider")?.value || 0);
		return o;
	}).filter(Boolean);

	if (isLogging) console.log("Output is:", output);
	sendData(JSON.stringify(output));
}

//Sends the Data to the Hub
function sendData(payload) {
	//console.log ("Sending Data");
	handleTransaction("begin");
    const url = '#URL#';
    fetch(`${url}&sessionID=${sessionID}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: payload }).catch(error => {
        console.error('Error:', error);
    });
}

// Function to fetch JSON data from a URL and return it
async function fetchData() {
  if (isLogging) { console.log("fetchData(): Downloading data from Hub.") };
  const url = '#URL#'; 
  
  // Cancel any ongoing fetch
  if (currentFetchController) {
    currentFetchController.abort();
  }
  
  // Create new controller
  currentFetchController = new AbortController();
  const signal = currentFetchController.signal;
  
  try {
    const response = await fetch(`${url}&sessionID=${sessionID}`, { signal });
    const jsonData = await response.json();
    return jsonData;
  } catch (error) {
    if (error.name === 'AbortError') {
      console.log("Fetch aborted");
    } else {
      console.error("Error fetching data:", error);
    }
    return null;
  } finally {
    currentFetchController = null;
  }
}

//***********************************************  onUpdate event handling   ***********************************************************
//**************************************************************************************************************************************

// Fan Speed change - Highlight the selected button
function speed(button) {
    const row = button.closest('tr');
    row.querySelectorAll('.button').forEach(btn => btn.classList.remove('selected'));
    button.classList.add('selected');
    lastCommand = "speed";
}

// Update the color value based on the input
function updateColor(colorInput) {
    const row = colorInput.closest('tr');
    const checkbox = row?.querySelector("input[type='checkbox']");

    // Update the current row's color display
    const colorDisplay = row?.querySelector('.color-display'); // Assuming there's an element to show the color
	//Set the color of this instance of the control
    if (colorDisplay) { colorDisplay.style.backgroundColor = colorInput.value; }

    // Sync the colors for other checked rows to this value
    if ( checkbox && checkbox.checked) { syncRows("color", colorInput.value); }

    lastCommand = "color"; // Track the last command
}

//Update the slider text label showing the selected value for level, position or CT
function updateSliderValue(slider, cmd) {
  const row = slider?.closest('tr'), cb = row?.querySelector("input[type='checkbox']");
  slider.nextElementSibling.innerText = slider.classList.contains('tilt-slider') ? `${slider.value}°` : `${slider.value}%`;
  if (cmd === "CT") row.dataset.colorMode = "CT";
  if (cmd === "tilt") updateAllTiltIndicators();
  if (cb?.checked) syncRows(cmd, slider.value);
  lastCommand = cmd;
}


// Toggle the first control column between Control A and Control B sliders and hide unused sliders
function toggleControl() {
    // Toggle the Control Group
    showSlider = showSlider === "A" ? "B" : "A";
    sessionStorage.setItem(storageKey("showSlider"), showSlider);

    // Update visibility for sliders and values
    document.querySelectorAll('tr').forEach(row => {
        const type = Number(row.dataset.type);

        // Determine visibility for each slider type
        const showLevel = showSlider === "A" && !(type <= 1 || type >= 10);
        const showCT = showSlider === "B" && (type === 3 || type === 4 || type === 5 || type >= 10);

        // Update visibility for Level sliders and values
        row.querySelectorAll('.level-slider, .level-value').forEach(el => { el.style.display = showLevel ? 'block' : 'none'; });

        // Update visibility for CT sliders and values
        row.querySelectorAll('.CT-slider, .CT-value').forEach(el => { el.style.display = showCT ? 'block' : 'none'; });
    });
}


//***********************************************  Table UI Management  ****************************************************************
//**************************************************************************************************************************************



//***********************************************  CheckBox Handling   *****************************************************************
//**************************************************************************************************************************************

function toggleRowSelection(checkbox) {
    const row = checkbox.closest('tr');
    const ID = row.dataset.ID;
    const checkboxStates = JSON.parse(sessionStorage.getItem(storageKey("checkboxStates"))) || {};    
    row.classList.toggle('selected-row', checkbox.checked);
    if (ID) checkboxStates[ID] = checkbox.checked;
    sessionStorage.setItem(storageKey("checkboxStates"), JSON.stringify(checkboxStates));
}

function toggleAllCheckboxes(masterCheckbox) {
    document.querySelectorAll('.option-checkbox').forEach(checkbox => {
        checkbox.checked = masterCheckbox.checked;
        toggleRowSelection(checkbox);
    });
}

function toggleChecked(label) {
	// Remove 'checked' class from all labels
	const labels = document.querySelectorAll('.radio-label');
	labels.forEach(lbl => lbl.classList.remove('checked'));
	// Add 'checked' class to the label
	label.classList.add('checked');
}

//Update the switches
function toggleSwitch(switchElement) {
	//console.log("Toggle command received");
    const row = switchElement.closest('tr');
    const newState = (switchElement.classList.toggle('on') ? 'on' : 'off');
    switchElement.dataset.state = newState;

    row?.querySelector('.radio-group')?.classList.toggle('disabled', newState === 'off');
    row?.querySelectorAll('.radio-group input[type="radio"]').forEach(input => input.disabled = newState === 'off');

    if (row?.querySelector("input[type='checkbox']")?.checked) syncRows("switch", newState);
    lastCommand = "switch";
}

//Takes any action initiated by the user and applies it to any other row that has the master checkbox checked.
function syncRows(command, value) {
	//console.log ("Received: ", command);

    document.querySelectorAll('.option-checkbox:checked').forEach(checkbox => {
        const row = checkbox.closest('tr');
        const updateElement = (selector, value) => {
            const element = row.querySelector(selector);
            if (element) element.value = value;
        };
        switch (command) {
            case "color":
                updateElement('input[type="color"]', value);
                row.dataset.colorMode = "RGB";
                break;
            case "level":
				if (row.querySelector('.level-slider')) { // Ensure the slider exists
     				updateElement('.level-slider', value);
        			row.querySelector('.level-value').innerText = value + '%';
    			};
				break;
			case "CT":
				if (row.querySelector('.CT-slider')) { // Ensure the slider exists
					updateElement('.CT-slider', value);
                	row.querySelector('.CT-value').innerText = value + '°K';
    			    }
				break;
			case "tilt":
				if (row.querySelector('.tilt-slider')) { // Ensure the slider exists
     				updateElement('.tilt-slider', value);
        			row.querySelector('.tilt-value').innerText = value + '%';
    			};
                break;
			case "position":
				if (row.querySelector('.shades-slider')) { // Ensure the slider exists
      				updateElement('.shades-slider', value);
        			row.querySelector('.shades-value').innerText = value + '%';
    			};
				if (row.querySelector('.blinds-slider')) { // Ensure the slider exists
      				updateElement('.blinds-slider', value);
        			row.querySelector('.blinds-value').innerText = value + '%';
    			};
				break;
			case "volume":
				if (row.querySelector('.volume-slider')) { // Ensure the slider exists
      				updateElement('.volume-slider', value);
        			row.querySelector('.volume-value').innerText = value + '%';
    			};
				break;
            case "switch":
                const toggleSwitch = row.querySelector('.toggle-switch');
                if (toggleSwitch) {
                    toggleSwitch.classList.toggle('on', value === 'on');
                    toggleSwitch.dataset.state = value;
                }
                break;
        }
    });
    lastCommand = "checked";
}


//***********************************************  Polling and Transactions  ***********************************************************
//**************************************************************************************************************************************

//Starts the Polling cycle
function startPolling(url, pollResult) {
  // If polling is already active, do not start again
  if (pollingTimeoutID) {
    if (isLogging) console.log("Polling already active, not starting again.");
    return;
  }

  async function pollLoop() {
    if (!isPolling) return; // External flag that disables polling globally

    if (!isSliding) {
      try {
        if (isLogging) console.log("Polling...");
        const response = await fetch(`${url}&sessionID=${sessionID}`);
        if (!response.ok) throw new Error(`Error: ${response.status}`);
        const data = await response.json();
        pollResult(data);
      } catch (error) {
        console.error("Polling error:", error);
        stopPolling(); // Always stop if an error occurs
        return;
      }
    } else if (isLogging) {
      console.log("Polling skipped due to slider interaction");
    }

    // Schedule the next poll
    pollingTimeoutID = setTimeout(pollLoop, pollInterval);
  }

  // Start polling loop
  pollingTimeoutID = setTimeout(pollLoop, 0); // start immediately
}

//Stops the Polling
function stopPolling() {
  if (pollingTimeoutID) {
    clearTimeout(pollingTimeoutID);
    pollingTimeoutID = null;
    if (isLogging) console.log("Polling stopped");
  }
}

//This is called when the JS App window loses or regains focus. In turn it stops or starts the polling cycle.
function checkAttention() {
  const active = !document.hidden;
  const now = new Date().toLocaleTimeString('en-US', { hour12: false });
  if (isLogging) console.log(`[${now}] Attention status:`, active ? "Applet Active" : "Applet Paused");

  if (active) {
    shuttle.style.animationPlayState = "running";
    startPolling('#URL1#', pollResult);
    if (isLogging) console.log(`[${now}] Polling started (if not already running).`);
  } else {
    shuttle.style.animationPlayState = "paused";
    stopPolling();
    if (isLogging) console.log(`[${now}] Polling stopped`);
  }
}

// This is the callback function. When the polling process receives a response, it comes here and we check if there is an update pending or not.
function pollResult(data) {
    const table = document.querySelector("table");
    if (data.update) {
        if (isLogging) console.log("Update is: True");
        handleTransaction("end", table);
        initialize();
        if (!table.classList.contains('glow-EffectSuccess')) {
            table.classList.remove('glow-EffectPending');
            table.classList.add('glow-EffectSuccess');
            setTimeout(() => {
                table.classList.remove('glow-EffectSuccess');
            }, #pollUpdateDuration#);
        }
    } else {
        if (isLogging) console.log("Update is: False");
        handleTransaction("check", table);
    }
}

//Tracks where we are in a transaction.
function handleTransaction(action, table) {
    switch (action) {
        case "begin":
            transaction = Date.now();
            isLogging && console.log("Transaction started:", transaction);
            break;

        case "end":
            transaction = null;
            isLogging && console.log("Transaction finished");
            break;

        case "check":
            if (!transaction) return;
            const elapsedTime = Date.now() - transaction;
            isLogging && console.log("Elapsed time:", elapsedTime);

            if (elapsedTime > transactionTimeout && !table.classList.contains('glow-EffectFail')) {
                isLogging && console.log("Transaction is late");
                table.classList.replace('glow-EffectPending', 'glow-EffectFail');
                setTimeout(() => {
                    handleTransaction("end", table);
                    table.classList.remove('glow-EffectFail');
                    initialize();
                }, #pollUpdateDuration#);
            } else {
                isLogging && console.log("Transaction is running");
            }
            break;
    }
}

//***********************************************  Sorting   ***************************************************************************
//**************************************************************************************************************************************

//Table sort function after condensing with AI.
function sortTable(i) {
    const tbody = document.querySelector("#sortableTable tbody");
    const rows = Array.from(tbody.rows);
    const val = (cell, sel) => cell?.querySelector(sel)?.value?.toLowerCase() || cell?.textContent?.trim().toLowerCase() || "";

	if (isCustomSort) {
	    rows.sort((a, b) => {
    	    const getGroupVal = row => {
        	    const cellIndex = row.cells.length - 2;
            	return parseFloat(val(row.cells[cellIndex], 'input')) || 0;
	        };
    	    return getGroupVal(a) - getGroupVal(b);
    	});
    	rows.forEach(row => tbody.appendChild(row));
	    if (i === 2) {
    	    intraGroupNumericSort(tbody);
        	setColumnHeaders(false);
        	return;
    	}
    	setColumnHeaders(false);
    	return;
	}

    if (i === -1) {
        i = sortDirection.activeColumn;
    } else {
        sortDirection.activeColumn = i;
        sortDirection.direction = sortDirection.direction === 'asc' ? 'desc' : 'asc';
        sessionStorage.setItem(storageKey("activeColumn"), i);
    }

    const dir = sortDirection.direction;
    localStorage.setItem(storageKey("sortDirection"), JSON.stringify(sortDirection));

    rows.sort((a, b) => {
        let a1 = val(a.cells[i], i === 3 ? '.toggle-switch' : 'input');
        let b1 = val(b.cells[i], i === 3 ? '.toggle-switch' : 'input');
        if (i === 3) {
            a1 = a.cells[i]?.querySelector('.toggle-switch')?.dataset.state || a1;
            b1 = b.cells[i]?.querySelector('.toggle-switch')?.dataset.state || b1;
        }
        if (i === 3 && a1 === b1) {
            return val(a.cells[2], 'input').localeCompare(val(b.cells[2], 'input'));
        }
        return dir === 'asc' ? a1.localeCompare(b1) : b1.localeCompare(a1);
    });
    tbody.append(...rows);
    setColumnHeaders(true);
}

function intraGroupNumericSort(tbody, targetGroup) {
    const rows = Array.from(tbody.rows);
    const segments = [];
    let current = null;
    rows.forEach(row => {
        if (Number(row.dataset.type) === groupRow) {
            current = { header: row, members: [], group: row.dataset.group };
            segments.push(current);
        } else if (current) {
            current.members.push(row);
        } else {
            segments.push({ header: null, members: [row], group: null });
        }
    });

    segments.forEach(seg => {
        if (seg.group !== targetGroup || !seg.header) return;

        // Check if state column values are numeric
        const isNumeric = seg.members.every(row => {
            const text = row.cells[3]?.textContent.trim().replace('%','').replace('W','');
            return text !== '' && !isNaN(parseFloat(text));
        });

        // Advance sort state — if not numeric skip states 1 and 2
        const current = intraGroupSortState[targetGroup] || 0;
        if (!isNumeric && current === 0) {
            intraGroupSortState[targetGroup] = 3;
        } else {
            intraGroupSortState[targetGroup] = (current % 3) + 1;
        }
        const sortState = intraGroupSortState[targetGroup];

        seg.members.sort((a, b) => {
            if (sortState === 3) {
                const nameA = a.cells[2]?.textContent.trim().toLowerCase() || '';
                const nameB = b.cells[2]?.textContent.trim().toLowerCase() || '';
                return nameA.localeCompare(nameB);
            } else {
                const valA = parseFloat(a.cells[3]?.textContent) || 0;
                const valB = parseFloat(b.cells[3]?.textContent) || 0;
                return sortState === 1 ? valB - valA : valA - valB;
            }
        });

        const hintSpan = seg.header.querySelector('.sort-hint');
        if (hintSpan) {
            hintSpan.textContent = sortState === 1 ? ' ▼' : sortState === 2 ? ' ▲' : ' ⇅';
        }

        let insertRef = seg.header;
        seg.members.forEach(row => {
            insertRef.after(row);
            insertRef = row;
        });

        if (sortState === 3) saveRowOrder();
    });
}


// Track sort state per subgroup: 1=high-low, 2=low-high, 3=alpha-name
function intraGroupNumericSortOld(tbody, targetGroup) {
    const rows = Array.from(tbody.rows);
    const segments = [];
    let current = null;
    rows.forEach(row => {
        if (Number(row.dataset.type) === groupRow) {
            current = { header: row, members: [], group: row.dataset.group };
            segments.push(current);
        } else if (current) {
            current.members.push(row);
        } else {
            segments.push({ header: null, members: [row], group: null });
        }
    });

    segments.forEach(seg => {
        if (seg.group !== targetGroup || !seg.header) return;

        intraGroupSortState[targetGroup] = ((intraGroupSortState[targetGroup] || 0) % 3) + 1;
        const sortState = intraGroupSortState[targetGroup];

        seg.members.sort((a, b) => {
            if (sortState === 3) {
                const nameA = a.cells[2]?.textContent.trim().toLowerCase() || '';
                const nameB = b.cells[2]?.textContent.trim().toLowerCase() || '';
                return nameA.localeCompare(nameB);
            } else {
                const valA = parseFloat(a.cells[3]?.textContent) || 0;
                const valB = parseFloat(b.cells[3]?.textContent) || 0;
                return sortState === 1 ? valB - valA : valA - valB;
            }
        });

        const hintSpan = seg.header.querySelector('.sort-hint');
        if (hintSpan) {
            hintSpan.textContent = sortState === 1 ? ' ▼' : sortState === 2 ? ' ▲' : ' ⇅';
        }

        let insertRef = seg.header;
        seg.members.forEach(row => {
            insertRef.after(row);
            insertRef = row;
        });
        
        // If we are in DragDrop mode then save the new order if it is Alpha based on Name
        if (sortState === 3  && isDragDrop ) saveRowOrder();
    });
}

// Function to set or clear column header sorting classes
function setColumnHeaders(applySorting = true) {
    const headers = document.querySelectorAll('#sortableTable thead th');

    // Remove sorting indicator classes from all headers
    headers.forEach(header => { header.classList.remove('ascSort', 'descSort'); });

    // If applySorting is true, mark the active column with the correct sort class
    if (applySorting && sortDirection.activeColumn !== undefined) {
        const activeHeader = headers[sortDirection.activeColumn];
        if (activeHeader) {
            activeHeader.classList.add(sortDirection.direction === 'asc' ? 'ascSort' : 'descSort');
        }
    }
}

function colorizeNumericSensors(rules) {
    const tbody = document.querySelector("#sortableTable tbody");
    rules.forEach(rule => {
        [...tbody.rows]
            .filter(r => Number(r.dataset.type) === rule.type)
            .forEach(row => {
                const stateCell = row.cells[3];
                const iconCell = row.cells[1];
                if (!stateCell) return;
                const val = parseFloat(stateCell.textContent.replace(/[^0-9.-]/g, ''));
                if (isNaN(val)) return;
                let color, weight;
                if (val >= rule.highThreshold) { color = rule.highColor; weight = 'normal'; }
                else if (val <= rule.lowThreshold) { color = rule.lowColor; weight = 'normal'; }
                else { color = rule.midColor; weight = 'normal'; }
                // Apply to state cell
                stateCell.style.color = color;
                stateCell.style.textShadow = 'none';
                stateCell.style.fontWeight = weight;
                // Apply to icon
                if (iconCell) {
                    const icon = iconCell.querySelector('.material-symbols-outlined');
                    if (icon) { 
                        icon.style.opacity = '1';
                        icon.classList.remove('bad', 'warn', 'good', 'off');
                        if (val >= rule.highThreshold || val <= rule.lowThreshold) { 
                            const r = parseInt(color.slice(1,3), 16);
                            const g = parseInt(color.slice(3,5), 16);
                            const b = parseInt(color.slice(5,7), 16);
                            icon.style.backgroundColor = `rgba(${r},${g},${b},0.5)`;
                            icon.style.color = '#000000';
                        } else { 
                            icon.style.backgroundColor = '';
                            icon.style.color = '';
                        }
                    }
                }
            });
    });
}


//***********************************************  Initialization  and Miscellaneous  **************************************************
//**************************************************************************************************************************************

//Add event listeners for control once the content is loaded
document.addEventListener("DOMContentLoaded", function() { 
	setupHeaderEventListeners();
	initialize(); 
	//Setup listeners used to pause the polling indicator when a user interacts with a slider.
	document.addEventListener("pointerdown", e => { if (e.target.matches('input[type="range"]')) { isSliding = true; shuttle.style.animationPlayState = "paused"; } });
    document.addEventListener("pointerup", e => { if (isSliding) { isSliding = false; shuttle.style.animationPlayState = "running"; } });
    document.addEventListener("pointercancel", () => { isSliding = false; shuttle.style.animationPlayState = "running"; });
	//The following listeners identify when the Window is inactive so polling can be paused.
    document.addEventListener("visibilitychange", checkAttention);
    window.addEventListener("focus", checkAttention);
    window.addEventListener("blur", checkAttention);
});

// Call the function and handle the returned data
function initialize() {
	if (isLogging) { console.log ("Initialize: fetching data") };
	fetchData().then(data => {
		if (data) {
			if (isLogging) { console.log("Fetched JSON Data:", data) };
			loadTableFromJSON(data);
			sortTable(-1 , null);  //-1 Indicates to use the saved sort order, Null - Indicates to use the last Header used.
		} else {
		if (isLogging) { console.log("Failed to fetch data.") };
		}
	});
}

//Performs a complete refresh of the page. // 1000 milliseconds = 1 second
function refreshPage(timeout) { 
    // Clean up event listeners
    removeHeaderEventListeners();
    setTimeout(function() { location.reload(true);  }, timeout);    
	// Abort any ongoing fetch operations
    if (currentFetchController) {
    	currentFetchController.abort();
    	currentFetchController = null;
    }
    if (pressTimer) {
	    clearTimeout(pressTimer);
	    pressTimer = null;
    }
}

//Converts RGB string to a Hex value
function rgb2hex(rgbString) {
    return '#' + rgbString.slice(4, -1).split(',').map(num => 
        ('0' + parseInt(num.trim()).toString(16)).slice(-2)
    ).join('');
}

// Add window beforeunload event listener for cleanup
window.addEventListener('beforeunload', function() {
	// Clean up resources
	if (isLogging) { console.log("Running cleanup process.") };
	removeHeaderEventListeners();
	if (currentFetchController) currentFetchController.abort();
	if (pressTimer) clearTimeout(pressTimer);
	if (pollingInterval) clearInterval(pollingInterval);
	//transactions.clear();
});

//alert ("V3");

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

