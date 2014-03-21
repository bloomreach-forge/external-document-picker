
CKEDITOR.dialog.add('exdocBrowserDialog', function(editor) {

  return {

    title : 'External Document Browser',
    minWidth : 400,
    minHeight : 200,

    contents : [
      {
        id : 'tab-linkinfo',
        label : 'Link Info',

        elements : [
          {
            type : 'text',
            id : 'searchQuery',
            label : 'Search Query',
          },
        ]
      },
      {
        id : 'tab-linktarget',
        label : 'Target',
        elements : [
          {
            type : 'text',
            id : 'target',
            label : 'Target'
          }
        ]
      }
    ],

    onOk : function() {
      var dialog = this;
      var attributes = {
        href: 'http://www.onehippo.org'
      };

      var sel = editor.getSelection();
      var range = sel && sel.getRanges()[ 0 ];

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
  };

});