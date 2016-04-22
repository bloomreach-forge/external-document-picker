
CKEDITOR.dialog.add('exdocBrowserDialog', function(editor) {

  var pickerConfig = editor.config.exdocpickerbase || {};

  var searchedDocs = [];

  return {

    title : pickerConfig.dialogTitle || 'External Document Browser',
    minWidth : pickerConfig.dialogMinWidth || 640,
    minHeight : pickerConfig.dialogMinHeight || 480,

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
                id : 'searchText',
              },
              {
                type : 'button',
                id : 'searchButton',
                label : 'Search',
                onClick: function() {
                  var dialog = this.getDialog();
                  var i;
                  var html;
                  if( pickerConfig.getSearchURL ) {
                    var query = dialog.getValueOf( 'tab-searchdocs', 'searchText' );
                    var searchURL = pickerConfig.getSearchURL( { 'query': query } );
                    $.get( searchURL, function( data ) {
                      searchedDocs = JSON.parse( data );
                      var docList = dialog.getContentElement( 'tab-searchdocs', 'docsList' );
                      var listView = dialog.getElement().findOne( '.listView' );
                      while( listView.getChildCount() !== 0 ) {
                        listView.getLast().remove();
                      }
                      if( !searchedDocs || searchedDocs.length === 0 ) {
                        listView.appendHtml( '<p>No search result.</p>' );
                      } else {
                        html = '<table>';
                        var docIcon;
                        var docTitle;
                        var docDesc;
                        for( i = 0; i < searchedDocs.length; i += 1 ) {
                          docIcon = searchedDocs[i].icon || pickerConfig.defaultDocumentIcon || '';
                          docTitle = searchedDocs[i].title || '';
                          docDesc = searchedDocs[i].description || '';
                          html += '<tr>';
                          html += '<td style="padding: 10px; vertical-align: middle">';
                          html += '<input type="radio" name="selectedDocument" value="' + i + '" id="selectedDocument' + i + '" />';
                          html += '</td>';
                          html += '<td style="padding: 10px; vertical-align: middle">';
                          html += '<label for="selectedDocument' + i + '"><img src="' + docIcon + '"/></label> ';
                          html += '</td>';
                          html += '<td style="padding: 10px; vertical-align: middle">';
                          html += '<label for="selectedDocument' + i + '"><h3>' + docTitle + '</h3></label>';
                          html += '<label for="selectedDocument' + i + '"><p>' + docDesc + '</p></label>';
                          html += '</td>'
                          html += '</tr>';
                        }
                        html += "</table>";
                        listView.appendHtml( html );
                      }
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
                html: '<form><div class="listView" style="HEIGHT: 400px; OVERFLOW: auto"><p>No search result.</p></div></form>'
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
      var selectedDocItem = dialog.getElement().findOne( '.listView input[name=selectedDocument]:checked' );
      var selectedDoc;

      if (selectedDocItem) {
        selectedDoc = searchedDocs[ parseInt( selectedDocItem.getAttribute( 'value' ) ) ] || {};

        if( pickerConfig.getLinkAttributes ) {
          var sel = editor.getSelection();
          var range = sel && sel.getRanges()[ 0 ];
          var attrs = pickerConfig.getLinkAttributes( selectedDoc ) || {};

          var target = dialog.getValueOf( 'tab-linktarget', 'target' );
          if( target ) {
            attrs[ 'target' ] = target;
          }

          if( range.collapsed ) {
            var link = editor.document.createElement( 'a', { attributes: attrs } );
            link.setText( selectedDoc.title )
            range.insertNode( link );
          } else {
            var style = new CKEDITOR.style( { element: 'a', attributes: attrs } );
            style.type = CKEDITOR.STYLE_INLINE;
            editor.applyStyle( style );
          }
        }
      }
    },

  };

});