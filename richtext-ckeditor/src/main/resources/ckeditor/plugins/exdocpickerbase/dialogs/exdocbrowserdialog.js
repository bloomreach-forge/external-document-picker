
CKEDITOR.dialog.add('exdocBrowserDialog', function(editor) {

  var pickerConfig = editor.config.exdocpickerbase || {};

  return {

    title : pickerConfig.dialogTitle || 'External Document Browser',
    minWidth : pickerConfig.dialogMinWidth || 640,
    minHeight : pickerConfig.dialogMinHeight || 480,

    searchedDocs: [],

    contents : [
      {
        id : 'tab-searchdocs',
        label : 'Search documents',

        elements : [
          {
            type: 'hbox',
            children: [
              {
                type : 'text',
                id : 'searchLabel',
              },
              {
                type : 'button',
                id : 'searchButton',
                label : 'Search',
                onClick: function() {
                  var dialog = this.getDialog();
                  if( pickerConfig.searchURL ) {
                    var data = CKEDITOR.ajax.load( pickerConfig.searchURL, function( data ) {
                      alert( data );
                      var docList = dialog.getContentElement( 'tab-searchdocs', 'docsList' );
                      var listView = dialog.getElement().findOne( '.listView' );
                    } );
                  }
                }
              },
            ]
          },
          {
            type: 'vbox',
            id: 'docsList',
            expand: false,
            children: [
              {
                type: 'html',
                html: '<div class="listView">No search result.</div>'
              },
            ]
          }
        ]
      },
      {
        id : 'tab-linktarget',
        label : 'Target',
        elements : [
          {
            type: 'select',
            id: 'target',
            label: 'Target',
            items : [ 
              [ '<not set>', '' ],
              [ 'New Window (_blank)', '_blank' ],
              [ 'Topmost Window (_top)', '_top' ],
              [ 'Same Window (_self)', '_self' ],
              [ 'Parent Window (_parent)', '_parent' ],
            ]
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
    },

  };

});