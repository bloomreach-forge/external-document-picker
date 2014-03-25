
CKEDITOR.dialog.add('exdocBrowserDialog', function(editor) {

  return {

    title : 'External Document Browser',
    minWidth : 640,
    minHeight : 480,

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

                  var data = CKEDITOR.ajax.load( 'exdocs.txt', function( data ) {
                    alert( data );
                    var docList = dialog.getContentElement( 'tab-searchdocs', 'docsList' );
                    var listView = dialog.getElement().findOne( '.listView' );
                    alert(listView);
                  } );
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