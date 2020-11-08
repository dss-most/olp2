var e1;

Ext.onReady(function(){
	Ext.QuickTips.init();
	
	
	Ext.define('Register', {
		extend : 'Ext.data.Model',
		fields : ['ID', 'REGISTER_NUMBER', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE', 'ACTIVITIES']
	});
	
	var registerStore = new Ext.data.Store({
		model: 'Register',
		id: 'store_registerStore',
		proxy: {
			type: 'ajax',
			url: appUrl('json/listRegister')
		}
	});
	
	var firstRegisterIdStore = new Ext.data.Store({
		model: 'Register',
		id: 'store_firstRegisterIdStore',
		proxy: {
			type: 'ajax',
			url: appUrl('json/listRegisterNumber')
		}
	});
	
	var lastRegisterIdStore = new Ext.data.Store({
		model: 'Register',
		id: 'store_lastRegisterIdStore',
		proxy: {
			type: 'ajax',
			url:  appUrl('json/listRegisterNumber')
		}
	});

	
	var simpleForm = Ext.create('Ext.form.Panel', {
		id : 'mainForm',
		url :  appUrl('reports/pdfReportByRegisterIds'),
		frame : true,
		title : 'พิมพ์ใบแจ้งหนี้ชำระค่าธรรมเนียม',
		bodyStyle : 'padding:5px 5px 0',
		standardSubmit: true,
		width : 850,
		fieldDefaults : {
			msgTarget : 'side',
			labelWidth : 150
		},
		defaultType : 'textfield',
		defaults : {
			anchor : '100%'
		},

		items : [ {
			fieldLabel : 'ปีงบประมาณ',
			name : 'fiscalYear',
			id : 'text_fiscalYear',
			minChars: 4,
			allowBlank : false,
			selectOnFocus : true,
			listeners: {
				change: function(field, newValue, oldValue, eOpts) {
					var registerIdStore = Ext.getStore('store_firstRegisterIdStore');
					registerIdStore.getProxy().url=appUrl("json/listRegisterNumber/" + newValue + "/");
					
					registerIdStore = Ext.getStore('store_lastRegisterIdStore');
					registerIdStore.getProxy().url=appUrl("json/listRegisterNumber/" + newValue + "/");
					
					loadGrid(newValue, Ext.getCmp("combo_firstRegisterNumber").value, Ext.getCmp("combo_lastRegisterNumber").value);
				}
			}
		}, {
			fieldLabel : 'หมายเลขลงทะเบียนเริ่มต้น',
			name : 'firstRegisterNumber',
			id : 'combo_firstRegisterNumber',
			xtype : 'combo',
			queryParam: 'query',
			displayField: 'REGISTER_NUMBER',
			store : firstRegisterIdStore,
			valueField : 'REGISTER_NUMBER',
			selectOnFocus : true,
			typeAhead : true,
			minChars : 0,
			queryMode : 'remote',
			listConfig: {
				getInnerTpl : function() {
					return '<div> <b>{REGISTER_NUMBER}</b> </div>';
				}
			},
			listeners : {
				change : function (oldValue, newValue) {
					loadGrid(Ext.getCmp("text_fiscalYear").value, newValue, Ext.getCmp("combo_lastRegisterNumber").value);
				}
			
			}
		
		},{
			fieldLabel : 'หมายเลขลงทะเบียนสิ้นสุด',
			name : 'LastRegisterNumber',
			id : 'combo_lastRegisterNumber',
			xtype : 'combo',
			queryParam: 'query',
			displayField: 'REGISTER_NUMBER',
			store : lastRegisterIdStore,
			valueField : 'REGISTER_NUMBER',
			selectOnFocus : true,
			typeAhead : true,
			minChars : 0,
			queryMode : 'remote',
			listConfig: {
				getInnerTpl : function() {
					return '<div> <b>{REGISTER_NUMBER}</b> </div>';
				}
			},
			listeners : { 
				change : function (oldValue, newValue) {
					
					loadGrid(Ext.getCmp("text_fiscalYear").value, Ext.getCmp("combo_firstRegisterNumber").value, newValue);
				}
			
			}
		
		},{
			xtype      : 'radiogroup',
            fieldLabel : 'ประเภท',
            defaultType: 'radiofield',
            defaults: {
                width: 90
            },
            listeners : {
            	change : function(field, newValue, oldValue ) {
            		if(newValue.activityType instanceof Array) return;
            		// now fire loadGrid
            		loadGrid(Ext.getCmp("text_fiscalYear").value, Ext.getCmp("combo_firstRegisterNumber").value, Ext.getCmp("combo_lastRegisterNumber").value);
            	}
            },
            layout: 'hbox',
            items: [
                {
                    boxLabel  : 'กิจกรรม',
                    name      : 'activityType',
                    inputValue: 'act',
                    id        : 'radio1'
                }, {
                    boxLabel  : 'QC-Sample',
                    name      : 'activityType',
                    inputValue: 'qc',
                    id        : 'radio2'
                }, {
                    boxLabel  : 'ทั้งหมด',
                    name      : 'activityType',
                    inputValue: 'all',
                    id        : 'radio3'
                }
            ]
	            
		}, {
			xtype: 'checkboxfield',
			name: 'chkbx_englishReport',
			boxLabel: 'ภาษาอังกฤษ',
			fieldLabel: 'พิมพ์ที่อยู่'
		}, {
			xtype: 'grid',
			id: 'grid_register',
			store: registerStore,
			height: 300,
			columns: [
			          {text: "เลขที่ลงทะเบียน", dataIndex: 'REGISTER_NUMBER', width: 120},
			          {text: "หมายเลขลูกค้า", dataIndex: 'CUSTOMER_CODE', width: 120},
			          {text: "ชื่อผู้ลงทะเบียน", dataIndex: 'CUSTOMER_NAME_CANDIDATE', width: 180},
			          {text: "กิจกรรมที่ลงทะเบียน", dataIndex: 'ACTIVITIES', flex: 1}
			],
			selModel: Ext.create('Ext.selection.CheckboxModel', {
				mode: 'MULTI',
				checkOnly: false,
				listeners: {
					selectionchange: function(rowModel, selected, eOpts) {
						
					}
				}

			}),
			frame:false,
			viewConfig: {
				emptyText: 'no data!'
			}
			
		}],

		buttons : [  {
			text : 'Excel Report สำหรับ EMS',
			id : 'btn_excelEms',
			listeners : {
				click : function() {
					var form = this.up('form');
					
					var selected = Ext.getCmp('grid_register').getSelectionModel().getSelection();
					
					var ids=[];
					
					for(var i=0; i<selected.length;i++) {
						ids.push(selected[i].data.ID);
					}

					
					
					form.getEl().dom.target = '_blank';
					
					
					form.submit({
						target: '_blank',
						params: {
							registerIds : ids,
							reportPage: 'excelEMSExport'
						}
					});
				}
					
			}

		},{
			text : 'พิมพ์ใบยืนยัน',
			id : 'btn_confirm',
			listeners : {
				click : function() {
					
					var form = this.up('form');
					
					var selected = Ext.getCmp('grid_register').getSelectionModel().getSelection();
					
					var ids=[];
					
					for(var i=0; i<selected.length;i++) {
						ids.push(selected[i].data.ID);
					}

					
					
					form.getEl().dom.target = '_blank';
					
					
					form.submit({
						target: '_blank',
						params: {
							registerIds : ids,
							reportPage: 'confirm2_letter'
						}
					});
				}
			}
		}, {
			text : 'พิมพ์ใบแจ้งหนี้',
			id : 'btn_quotation',
			listeners : {
				click : function() {
					
					var form = this.up('form');
					
					var selected = Ext.getCmp('grid_register').getSelectionModel().getSelection();
					
					var ids=[];
					
					for(var i=0; i<selected.length;i++) {
						ids.push(selected[i].data.ID);
					}

					
					form.submit({
						target: '_blank',
						params: {
							registerIds : ids,
							reportPage: 'quotationReport'
						}
					
					});
				}
			}
		}]
	});

	function loadGrid(fiscalYear, firstRegister, lastRegister) {
		var grid = Ext.getCmp('grid_register');
		grid.getStore().load({
			params: {
				fiscalYear: fiscalYear,
				firstRegisterNumber: firstRegister,
				lastRegisterNumber: lastRegister,
				activityType: Ext.getCmp('radio1').getGroupValue()
			}
		});
		
	}
	
	//var el = simpleForm.getEl();
	//el.dom.target = '_blank';
	simpleForm.render('formCanvas');
	Ext.getCmp('radio3').setValue(true);
	 
});