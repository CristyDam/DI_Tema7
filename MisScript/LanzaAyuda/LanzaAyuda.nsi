;NSIS Modern User Interface
;Escrito por Cristina Fernandez

;--------------------------------
;Include Modern UI

!include "MUI2.nsh"
;!include "ZipDLL.nsh"
;--------------------------------
;General

;Nombre y fichero
Name "LanzaAyuda"
OutFile "InstaladorLanzaAyuda.exe"
Unicode True

;Carpeta de instalacion predeterminada
InstallDir "$LOCALAPPDATA\LanzaAyuda"
  
;Obtener la carpeta de instalacion del registro si esta disponible
InstallDirRegKey HKCU "Software\LanzaAyuda" ""

;Solicitar privilegios de aplicación para Windows Vista
RequestExecutionLevel admin

;--------------------------------
;Variables

  Var StartMenuFolder

;--------------------------------
;Configuración de la interfaz

!define MUI_ABORTWARNING
!define MUI_HEADERIMAGE

;---------------------------------
!define MUI_HEADERIMAGE_BITMAP "IESMontecillos.bmp" ; optional

!define !define MUI_HEADERIMAGE_RIGHT

;--------------------------------------
;Pages

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "licencia.txt"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_UNPAGE_FINISH
  
;Configuración de la página de la carpeta del menú Inicio
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\LanzaAyuda" 
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  
;--------------------------------
;Languages
 
!insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Secciones del instalador

Section "App Lanza Ayuda" SecDummy

SetOutPath "$INSTDIR"

;AGREGUE SUS PROPIOS ARCHIVOS AQUÍ ...
File LanzaAyuda.7z
Nsis7z::Extract "LanzaAyuda.7z"
Delete "LanzaAyuda.7z"

;Carpeta de instalación de la tienda
WriteRegStr HKCU "Software\LanzaAyuda" "" $INSTDIR

;Crear desinstalador
WriteUninstaller "$INSTDIR\UninstallLanzaAyuda.exe"

!insertmacro MUI_STARTMENU_WRITE_BEGIN Application

;Crear accesos directos(acceso directo Window)
;Create shortcuts
CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
CreateShortcut "$SMPROGRAMS\$StartMenuFolder\LanzaAyuda.lnk" "$INSTDIR\sLanzaAyuda.jar"
CreateShortcut "$SMPROGRAMS\$StartMenuFolder\UninstallLanzaAyuda.lnk" "$INSTDIR\UninstallLanzaAyuda.exe"

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

Section "UninstallLanzaAyuda"

;AGREGUE SUS PROPIOS ARCHIVOS AQUÍ ...

Delete "$INSTDIR\UninstallLanzaAyuda.exe"

RMDir /r /REBOOTOK "$INSTDIR"
  
!insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder

;elimina el link 
Delete "$SMPROGRAMS\$StartMenuFolder\LanzaAyuda.lnk"  
Delete "$SMPROGRAMS\$StartMenuFolder\UninstallLanzaAyuda.lnk"

;elimina el directorio
RMDir "$SMPROGRAMS\$StartMenuFolder"
  
DeleteRegKey /ifempty HKCU "Software\LanzaAyuda"

SectionEnd
