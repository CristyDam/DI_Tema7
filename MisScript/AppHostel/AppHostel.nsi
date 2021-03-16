;NSIS Modern User Interface
;Escrito por Cristina Fernandez

;--------------------------------
;Include Modern UI

!include "MUI2.nsh"

;--------------------------------
;General

;Nombre y fichero
Name "AppHostel"
OutFile "InstaladorAppHostel.exe"
Unicode True


;Carpeta de instalacion predeterminada
InstallDir "$LOCALAPPDATA\AppHostel"
  
;Obtener la carpeta de instalacion del registro si esta disponible
InstallDirRegKey HKCU "Software\AppHostel" ""

;Solicitar privilegios de aplicación para Windows Vista
RequestExecutionLevel admin

;--------------------------------
;Variables

Var AppHostelJar

;--------------------------------

;Configuración de la interfaz

!define MUI_ABORTWARNING
;!define MUI_HEADERIMAGE

;---------------------------------
!define MUI_HEADERIMAGE_BITMAP "logoDingo.bmp" ; optional

!define !define MUI_HEADERIMAGE_RIGHT

;--------------------------------------
;Pages

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "licencia.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
;!insertmacro MUI_PAGE_FINISH

;!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
;!insertmacro MUI_UNPAGE_FINISH
  
;Configuración de la página de la carpeta del menú Inicio
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\AppHostel" 
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
!insertmacro MUI_PAGE_STARTMENU Application $AppHostelJar
  
;--------------------------------
;Languages
 
!insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Secciones del instalador

Section "App Hostel" SecDummy

SetOutPath "$INSTDIR"

;AGREGUE SUS PROPIOS ARCHIVOS AQUÍ ...
File "AppHostel.7z"
Nsis7z::Extract "AppHotel.7z"
Delete "AppHotel.7z"
;!insertmacro ZIPDLL_EXTRACT "$INSTDIR\AppHostel.zip" "$INSTDIR" "<ALL>"

;Carpeta de instalación de la tienda
WriteRegStr HKCU "Software\AppHostel" "" $INSTDIR

;Crear desinstalador
WriteUninstaller "$INSTDIR\UninstallAppHostel.exe"

!insertmacro MUI_STARTMENU_WRITE_BEGIN Application

;Crear accesos directos(acceso directo Window)
CreateDirectory "$SMPROGRAMS\$AppHostelJar"
CreateShortcut "$SMPROGRAMS\$AppHostelJar\AppHostel.lnk" "$INSTDIR\sAppHostel.jar"
CreateShortcut "$PROGRAMFILES32\$StartMenuFolder\UninstallAppHostel.lnk" "$INSTDIR\UninstallAppHostel.exe"

!insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

;--------------------------------
;Descripcion

;Cadenas de idioma
LangString DESC_SecDummy ${LANG_ENGLISH} "A test section."

;Asignar cadenas de idioma a las secciones
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
!insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
!insertmacro MUI_FUNCTION_DESCRIPTION_END
 
;--------------------------------
;Sección del desinstalador

Section "Uninstall"

;AGREGUE SUS PROPIOS ARCHIVOS AQUÍ ...

;Delete $INSTDIR\lib\.....
RMDir /r /REBOOTOK "$INSTDIR"

!insertmacro MUI_STARTMENU_GETFOLDER Application $AppHostelJar

;elimina el link 
Delete "$SMPROGRAMS\$AppHostelJar\AppHostel.lnk"  
Delete "$SMPROGRAMS\$AppHostelJar\Uninstall.lnk"
;elimina el directorio
RMDir "$SMPROGRAMS\$AppHostelJar"
  
DeleteRegKey /ifempty HKCU "Software\AppHostel"

SectionEnd
