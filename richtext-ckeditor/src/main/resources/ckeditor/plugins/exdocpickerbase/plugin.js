CKEDITOR.plugins.add( 'exdocpickerbase', {

  icons: 'exdocpickerbase',

  init: function( editor ) {

    editor.addCommand( 'linkToExternalDocument', {

      exec: function( editor ) {

        var attributes = {
          href: 'http://www.onehippo.org'
        };

        var sel = editor.getSelection(),
        range = sel && sel.getRanges()[ 0 ];

        if ( range.collapsed ) {
          var link = editor.document.createElement( 'a', { attributes: attributes } );
          link.setText('Test External Document')
          range.insertNode( link );
        } else {
          var style = new CKEDITOR.style( { element: 'a', attributes: attributes } );
          style.type = CKEDITOR.STYLE_INLINE;
          editor.applyStyle( style );
        }
      }

    });

    editor.ui.addButton( 'LinkToExternalDocument', {
      label: 'Link to External Document',
      command: 'linkToExternalDocument',
      toolbar: 'links'
    });

  }

});