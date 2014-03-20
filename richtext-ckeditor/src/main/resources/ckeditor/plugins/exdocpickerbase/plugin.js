CKEDITOR.plugins.add( 'exdocpickerbase', {

  icons: 'exdocpickerbase',

  init: function( editor ) {

    editor.addCommand( 'linkToExternalDocument', {
      exec: function( editor ) {
        var now = new Date();
        editor.insertHtml( 'The current date and time is: <em>' + now.toString() + '</em>' );
      }
    });

    editor.ui.addButton( 'LinkToExternalDocument', {
      label: 'Link to External Document',
      command: 'linkToExternalDocument',
      toolbar: 'links'
    });

  }

});