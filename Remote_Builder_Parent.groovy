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
*  Version 1.0.2 - Added Roku Module
*
*  Gary Milne - 9/4/24 @ 7:05 AM
*
*  Pending Improvements: None at this time.
*
**/

import groovy.transform.Field
@Field static final Version = "<b>Remote Builder Parent v1.0.2 (9/4/24)</b>"

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
    if (state.initialized == null ) initialize()
    
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
                
                myString = "You are installing <b>Remote Builder Standard which is free.</b> The standard version gives access to the <b>Fixed 6 Button Remote</b> where actions can be customized but the appearance of the Remote is fixed.<br>"
				myString += "The Advanced version gives you access to the <b>Custom 6 Button Remote</b> which support 18 buttons in three groups. All aspects of the remote appearance are also fully customizable.<br>"
				myString += "The Advanced version also includes the TV Remote which gives you a fully configured TV Remote (shown below) which you can connect to your Hubitat TV Driver for a seamless experience. Other remotes will be added in the near future.<br>"
                myString += "If you wish to upgrade to <b>Remote Builder Advanced</b> you can do so after setup is complete by visiting the Licensing section. The Advanced version gives you access to a variety of Remotes.<br>"
				myString += "<b>Remote Builder</b> and <b>Tile Builder</b> are sister products. An Advanced license for either one grants you full access to the other."
                paragraph myString
				
				//Get the sample table
                myHTML = getSample()
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
                    myString += "Fixed 6 Button Remote (6 commands). Button actions can be customized but the interface cannot be.<br><br>"
                    myString += "<b>Remote Builder Advanced (Donation Required)</b><br>"
                    myString += "<b>1)</b> Customizable 6 Button Remote with 3 X button groups (18 commands). Highly customizable for button color, text, tooltips and background.<br>"
					myString += "<b>2)</b> Universal TV Remote shown earlier with user defineable buttons for quick access.<br>"
					myString += "<b>3)</b> More to come!<br><br>"
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
                    //paragraph ("isStorageConnected: $state.isStorageConnected")
                    
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
                    myString = '<b>Remote Builder</b> currently has three types of remotes:<br>'
                    myString += '<b>1)</b> Fixed 6 Button Remote. (Standard)<br>'
					myString += '<b>2)</b> Roku Remote. (Standard)<br>'
                    myString += '<b>3)</b> Custom 6 Button Remote with 3 x button groups for 18 unique actions. (Advanced)<br>'
                    myString += '<b>4)</b> TV Remote with custom buttons. (Advanced)<br>'
					myString += '<b>More remotes will be added soon.</b>'
                    paragraph note('', myString)
                    
					//Advanced only items use the checkLicense() function to tell if they should be displayed.
                    if (!hideFixed6ButtonRemote) app (name: 'TBPA', appName: 'Remote Builder - Fixed 6 Button', namespace: 'garyjmilne', title: 'Add Fixed Six Button Remote')
					if (!hideCustom6ButtonRemote && checkLicense() ) app (name: 'TBPA', appName: 'Remote Builder - Custom 6 Button', namespace: 'garyjmilne', title: 'Add Custom Six Button Remote')
					//if (!hideKeypad) app (name: 'TBPA', appName: 'Remote Builder - Keypad', namespace: 'garyjmilne', title: 'Add Keypad')
					if (!hideRokuRemote) app (name: 'TBPA', appName: 'Remote Builder - Roku', namespace: 'garyjmilne', title: 'Add Roku Remote')
					if (!hideTVRemote && checkLicense() ) app (name: 'TBPA', appName: 'Remote Builder - TV', namespace: 'garyjmilne', title: 'Add TV Remote')
					
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
                    input (name: "isLogDebug", type: "bool", title: "<b>Enable debug logging?</b>", defaultValue: false, submitOnChange: true, width: 2)
                    input (name: "isLogWarn",  type: "bool", title: "<b>Enable warn logging?</b>", defaultValue: true, submitOnChange: true, width: 2)
                    input (name: "isLogError",  type: "bool", title: "<b>Enable error logging?</b>", defaultValue: true, submitOnChange: true, width: 2, newLineAfter: true)
                    paragraph line(1)
                    
                    paragraph body('<b>Show/Hide Modules</b>')
                    input (name: "hideFixed6ButtonRemote", type: "bool", title: "<b>Hide Fixed 6 Button Remote?</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideCustom6ButtonRemote",  type: "bool", title: "<b>Hide Custom 6 Button Remote</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideRokuRemote", type: "bool", title: "<b>Hide Roku Remote?</b>", defaultValue: false, submitOnChange: true, width: 2)
					input (name: "hideTVRemote", type: "bool", title: "<b>Hide TV Remote?</b>", defaultValue: false, submitOnChange: true, width: 2)
					//input (name: "hideKeypad",  type: "bool", title: "<b>Hide Keypad</b>", defaultValue: false, submitOnChange: true, width: 2)
                    paragraph line(1)
                    
                    input(name: 'removeLicense'  , type: 'button', title: 'De-Activate Software License', backgroundColor: '#27ae61', textColor: 'white', submitOnChange: true, width: 3, newLineAfter: true)
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
            myText += '<div style="text-align:center;font-weight:small;font-size:12px">Version: ' + Version + '</div>'
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

def initialize() {
    if (isLogTrace) log.trace ('initialized: Entering initialize')
    if (isLogInfo) log.info ('Running Initialize.')
    if ( state.initialized == true ) {
        if (isLogInfo) log.info ('initialize has already been run. Exiting')
        return
    }
    
	//Set the flag so that this should only ever run once.
    state.initialized = true

    //Set initial Log settings
    app.updateSetting('isLogDebug', false)
    app.updateSetting('isLogTrace', false)
    app.updateSetting('isLogInfo', false)
    app.updateSetting('isLogWarn', true)
    app.updateSetting('isLogError', true)

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
    state.selectedDeviceHistory = [new: 'seed1', old: 'seed']
    state.isAdvancedLicense = false
    state.activationState = "Not Activated"
    
    app.updateSetting("myInput", [value:"#c61010", type:"color"])
    app.updateSetting('selectedDevice', 'Remote Builder Storage Device 1')
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



def getSample(){

	def myHTML = """
<svg
  width="180"
  height="420"
  viewBox="0 0 180 420"
  xmlns="http://www.w3.org/2000/svg"
>

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
	<linearGradient id="gradient1" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="cyan" />
      <stop offset="100%" stop-color="magenta" />
    </linearGradient>
  </defs>
    
  <style>
    .button-text {
      font-family: Arial;
      font-size: 18px;
      fill: white;
      text-anchor: middle;
      dominant-baseline: middle;
    }
	
	.button-text-small {	
		font-size:12px;
	}
	
	.button-text-large {	
		font-size:24px;
	}
	
	.button-text-gradient {
		font-family: Arial;
		font-size: 30px;
		text-anchor: middle;
		dominant-baseline: middle;
		font-weight: 700; 
	}
	
	.custom-button {
	opacity: 0.5;
	text-shadow: 2px 2px #000;
	}
  </style>

  <!-- Remote body -->
  <rect x="10" y="10" width="160" height="400" rx="20" ry="20" fill="#333" stroke="#222" stroke-width="5" />

  <!-- Power button -->
  <circle cx="40" cy="40" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
  <text x="40" y="40" class="button-text">⚡</text>
  
  <!-- LED -->
  <circle id="led1" cx="50%" cy="7%" r="2%" fill="#555"/>
  
  <!-- Source Button -->
  <circle cx="140" cy="40" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
  <text x="140" y="40" class="button-text" style="fill:red" >☰</text>
  
  <!-- Navigation buttons -->
  <circle cx="90" cy="120" r="50" fill="#444" stroke="url(#shadow-effect)" />
  <text x="90" y="85" class="button-text"> ▲ </text>
  <text x="90" y="155" class="button-text" >▼</text>
  <text x="55" y="120" class="button-text" >◀</text>
  <text x="125" y="120" class="button-text" > ▶ </text>
    
  <!-- Volume Controls -->
  <rect x="25" y="180" width="30" height="80" rx="20" ry="20" fill="#333" stroke="url(#shadow-effect)" stroke-width="0.5" />
  <text x="40" y="195" class="button-text" >▲</text>
  <text x="40" y="220" class="button-text button-text-small">VOL</text>
  <text x="40" y="245" class="button-text">▼</text>
  
  <!-- Channel Controls -->
  <rect x="125" y="180" width="30" height="80" rx="20" ry="20" fill="#333" stroke="url(#shadow-effect)" stroke-width="0.5" />
  <text x="140" y="195" class="button-text " >▲</text>
  <text x="140" y="220" class="button-text button-text-small">CH.</text>
  <text x="140" y="245" class="button-text ">▼</text>
  
  <!-- OK button -->
  <circle cx="90" cy="120" r="15" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1" />
  <text x="90" y="122" class="button-text button-text-small">OK</text>

  <!-- Mute button -->
  <circle cx="90" cy="220" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
  <text x="90" y="220" class="button-text">🔇</text>
  
  <!-- Home button -->
  <circle cx="90" cy="300" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
  <text x="91" y="299" class="button-text-gradient" fill="url(#gradient1)">⌂</text>
  
  <!-- Back button -->
  <circle cx="40" cy="300" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
  <text x="40" y="302" class="button-text button-text-large">↩</text>
  
  <!-- Gear button -->
  <circle cx="140" cy="300" r="20" fill="url(#vertical-gradient)" stroke="url(#shadow-effect)" stroke-width="1"/>
  <text x="140" y="291" class="button-text" style="font-size:12px">⚙️</text>
  <text x="140" y="300" class="button-text" style="font-size:5px">🔴🟢🟡🔵</text>
  <text x="140" y="309" class="button-text" style="font-size:10px">123</text>
  
  <!-- Custom buttons -->
  <circle cx="35" cy="350" r="12" fill="red" stroke="gray" stroke-width="1"/>
  <text x="35" y="351" class="button-text custom-button">A</text>
  
  <circle cx="75" cy="350" r="12" fill="green" stroke="gray" stroke-width="1"/>
  <text x="75" y="351" class="button-text custom-button">B</text>
  
  <circle cx="110" cy="350" r="12" fill="purple" stroke="gray" stroke-width="1"/>
  <text x="110" y="351" class="button-text custom-button">C</text>
  
  <circle cx="145" cy="350" r="12" fill="blue" stroke="gray" stroke-width="1"/>
  <text x="145" y="351" class="button-text custom-button">D</text>
  
  <!-- Custom buttons -->
  <circle cx="35" cy="390" r="12" fill="red" stroke="gray" stroke-width="1"/>
  <text x="35" y="391" class="button-text custom-button">1</text>
  
  <circle cx="75" cy="390" r="12" fill="green" stroke="gray" stroke-width="1"/>
  <text x="75" y="391" class="button-text custom-button">2</text>
  
  <circle cx="110" cy="390" r="12" fill="purple" stroke="gray" stroke-width="1"/>
  <text x="110" y="391" class="button-text custom-button">3</text>
  
  <circle cx="145" cy="390" r="12" fill="blue" stroke="gray" stroke-width="1"/>
  <text x="145" y="391" class="button-text custom-button">4</text>
  
</svg>
"""
	
	return myHTML
}