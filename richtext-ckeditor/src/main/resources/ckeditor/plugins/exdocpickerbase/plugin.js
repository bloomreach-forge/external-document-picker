CKEDITOR.plugins.add( 'exdocpickerbase', {

  icons: 'exdocpickerbase',

  init: function( editor ) {

    var pickerConfig = editor.config.exdocpickerbase || {};

    // Because Hippo CMS7 CKEditor configuration in JSON (e.g, "ckeditor.config.overlayed.json" and "ckeditor.config.appended.json")
    // doesn't support non-string type parameters such as function, we have to convert the function string to a real js function.
    if( pickerConfig.getSearchURL ) {
      if( typeof pickerConfig.getSearchURL == 'string' ) {
        pickerConfig.getSearchURL = new Function('data', pickerConfig.getSearchURL);
      }
    }
    if( pickerConfig.getLinkAttributes ) {
      if( typeof pickerConfig.getLinkAttributes == 'string' ) {
        pickerConfig.getLinkAttributes = new Function('item', pickerConfig.getLinkAttributes);
      }
    }

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