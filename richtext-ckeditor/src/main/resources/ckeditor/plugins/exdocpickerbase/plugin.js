CKEDITOR.plugins.add( 'exdocpickerbase', {

  icons: 'exdocpickerbase',

  init: function( editor ) {

    var pickerConfig = editor.config.exdocpickerbase || {};

    editor.addCommand( 'linkToExternalDocument', new CKEDITOR.dialogCommand( 'exdocBrowserDialog' ) );

    editor.ui.addButton( 'LinkToExternalDocument', {
      label: pickerConfig.buttonLabel || 'Link to External Document',
      icon: pickerConfig.buttonIcon || this.path + 'icons/exdocpickerbase.png',
      command: 'linkToExternalDocument',
      toolbar: 'links'
    });

    CKEDITOR.dialog.add( 'exdocBrowserDialog', this.path + 'dialogs/exdocbrowserdialog.js' );
  }

});