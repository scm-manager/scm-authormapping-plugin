/*
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://bitbucket.org/sdorra/scm-manager
 * 
 */

Ext.ns('Sonia.authormapping');

Sonia.authormapping.ConfigPanel = Ext.extend(Sonia.repository.PropertiesFormPanel, {
  
  mappingStore: null,
  
  // labels
  formTitleText: 'Author Mapping',
  colNameText: 'Name',
  colDisplayNameText: 'Display Name',
  colMailText: 'Mail',
  
  authorMappingGridHelpText: 'Manage mappings for changeset authors.<br />\n\
    <strong>Name</strong> = Author name to map.<br />\n\
    <strong>Display Name</strong> = New authors display name.<br />\n\
    <strong>Mail</strong> = New authors mail.',
  
  addText: 'Add',
  removeTest: 'Remove',

  // icons
  addIcon: 'resources/images/add.gif',
  removeIcon: 'resources/images/delete.gif',
  
  initComponent: function(){
    this.mappingStore = new Ext.data.ArrayStore({
      root: 'mappings',
      fields: [
        {name: 'name'},
        {name: 'displayName'},
        {name: 'mail'}
      ],
      sortInfo: {
        field: 'name'
      }
    });
    
    this.loadAuthorMappings(this.mappingStore, this.item);
    
    
    var mappingsColModel = new Ext.grid.ColumnModel({
      defaults: {
        sortable: true,
        editable: true
      },
      columns: [{
        id: 'name',
        dataIndex: 'name',
        header: this.colNameText,
        editor: Ext.form.TextField
      },{
        id: 'displayName',
        dataIndex: 'displayName',
        header: this.colDisplayNameText,
        editor: Ext.form.TextField
      },{
        id: 'mail',
        dataIndex: 'mail',
        header: this.colMailText,
        editor: Ext.form.TextField
      }]
    });
    
    var selectionModel = new Ext.grid.RowSelectionModel({
      singleSelect: true
    });
    
    var config = {
      title: this.formTitleText,
      items: [{
        id: 'authorMappingGrid',
        xtype: 'editorgrid',
        clicksToEdit: 1,
        autoExpandColumn: 'displayName',
        frame: true,
        width: '100%',
        autoHeight: true,
        autoScroll: false,
        colModel: mappingsColModel,
        sm: selectionModel,
        store: this.mappingStore,
        viewConfig: {
          forceFit:true
        },
        tbar: [{
          text: this.addText,
          scope: this,
          icon: this.addIcon,
          handler : function(){
            var Mapping = this.mappingStore.recordType;
            var p = new Mapping();
            var grid = Ext.getCmp('authorMappingGrid');
            grid.stopEditing();
            this.mappingStore.insert(0, p);
            grid.startEditing(0, 0);
          }
        },{
          text: this.removeText,
          scope: this,
          icon: this.removeIcon,
          handler: function(){
            var grid = Ext.getCmp('authorMappingGrid');
            var selected = grid.getSelectionModel().getSelected();
            if ( selected ){
              this.mappingStore.remove(selected);
            }
          }
        }, '->',{
          id: 'authorMappingGridHelp',
          xtype: 'box',
          autoEl: {
            tag: 'img',
            src: 'resources/images/help.gif'
          }
        }]

      }]
    }
    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.authormapping.ConfigPanel.superclass.initComponent.apply(this, arguments);
  },
  
  afterRender: function(){
    // call super
    Sonia.authormapping.ConfigPanel.superclass.afterRender.apply(this, arguments);

    Ext.QuickTips.register({
      target: Ext.getCmp('authorMappingGridHelp'),
      title: '',
      text: this.authorMappingGridHelpText,
      enabled: true
    });
  },
 
  loadAuthorMappings: function(store, repository){
    if (debug){
      console.debug('load author mapping properties');
    }
    if (!repository.properties){
      repository.properties = [];
    }
    Ext.each(repository.properties, function(prop){
      if ( prop.key == 'sonia.authormapping.manual-mapping' ){
        var value = prop.value;
        this.parseMappings(store, value);
      }
    }, this);
  },
  
  parseMappings: function(store, mappingString){
    var parts = mappingString.split(';');
    Ext.each(parts, function(part){
      var pa = part.split(',');
      if ( pa.length > 0 && pa[0].length > 0 ){
        var mail = '';
        if ( pa.length > 1 ){
          mail = pa[2].trim();
        }
        
        var Mapping = store.recordType;
        var am = new Mapping({
          name: pa[0].trim(),
          displayName: pa[1].trim(),
          mail: mail
        });
        
        if (debug){
          console.debug('add author mapping: ');
          console.debug( am );
        }
        store.add(am);
      }
    });
  },
  
  storeExtraProperties: function(repository){
    if (debug){
      console.debug('store author mapping properties');
    }
    
    // delete old sub repositories
    Ext.each(repository.properties, function(prop, index){
      if ( prop.key == 'sonia.authormapping.manual-mapping' ){
        delete repository.properties[index];
      }
    });
    
    var mappingsString = '';
    this.mappingStore.data.each(function(r){
      var am = r.data;
      mappingsString += am.name + ',' + am.displayName + ',' + am.mail + ';';
    });
    
    if (debug){
      console.debug('add author mapping string: ' + mappingsString);
    }
    
    repository.properties.push({
      key: 'sonia.authormapping.manual-mapping',
      value: mappingsString
    });
  }
  
  
});

// register xtype
Ext.reg("authormappingConfigPanel", Sonia.authormapping.ConfigPanel);

// register panel
Sonia.repository.openListeners.push(function(repository, panels){
  panels.push({
    xtype: 'authormappingConfigPanel',
    item: repository
  });
});

