	
var currentFiscalYear;
Ext.onReady(function(){
	Ext.QuickTips.init();
	
	Ext.define('Activity', {
		extend: 'Ext.data.Model',
		fields: ['ID', 'ACTIVITY_NAME', 'ACTIVITY_CODE', 'BRANCH_NAME', 'EXAMPLE_NAME', 'REGISTER_NUMBER']
	});
	
	Ext.define('Customer', {
		extend : 'Ext.data.Model',
		fields : ['ID', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE']
	});
	
	Ext.define('Register', {
		extend : 'Ext.data.Model',
		fields : ['ID', 'REGISTER_NUMBER', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE', 'ACTIVITIES']
	});

	
	var registerStore = new Ext.data.Store({
		model: 'Register',
		id: 'store_registerStore',
		proxy: {
			type: 'ajax',
			url: appUrl('/json/listRegister')
		}
	});
	
	var activityStore = new Ext.data.Store({
		model : 'Activity',
		id: 'store_activityStore',
		groupField: 'BRANCH_NAME',
		proxy : {
			type : 'ajax',
			url : appUrl('/json/listActivity')
		}
	});
	
	var groupingFeature = Ext.create('Ext.grid.feature.Grouping',{
        groupHeaderTpl: '{name} : ({[values.rows.length]} กิจกรรม)'
    });
	
	var customerStore = new Ext.data.Store({
		model : 'Customer',
		id : 'store_customerStore',
		proxy : {
			type : 'ajax',
			url : appUrl('/json/listCustomer')
		}
	});

	var simpleForm = Ext.create('Ext.form.Panel', {
		id : 'mainForm',
		url : 'printInvoice',
		frame : true,
		title : 'พิมพ์ใบแจ้งหนี้ชำระค่าธรรมเนียม',
		bodyStyle : 'padding:5px 5px 0',
		standardSubmit: true,
		width : 850,
		fieldDefaults : {
			msgTarget : 'side',
			labelWidth : 75
		},
		defaultType : 'textfield',
		defaults : {
			anchor : '100%'
		},

		items : [ {
			fieldLabel : 'ปีงบประมาณ',
			name : 'fiscalYear',
			id : 'text_fiscalYear',
			allowBlank : false,
			selectOnFocus : true,
			listeners: {
				change: function(field, newValue, oldValue, eOpts) {
					var g = Ext.getCmp('grid_activity');
					var s = g.getStore();
					s.load({
						params: {
							fiscalYear: newValue
						}
					});
					
					//now set url for customer Store
					Ext.getStore('store_customerStore').setProxy({type:'ajax', url:appUrl('/json/listCustomer/')+newValue});
					currentFiscalYear = newValue;
				}
			}
		}, {
			fieldLabel : 'รหัสลูกค้า',
			name : 'customerCode',
			id : 'combo_customerCodeCombo',
			xtype : 'combo',
			queryParam: 'customerCode',
			displayField: 'CUSTOMER_CODE',
			store : customerStore,
			valueField : 'ID',
			typeAhead : true,
			valueNotFoundText : "ไม่พบรหัสลูกค้าลงทะเบียนในปีนี้",
			minChars : 2,
			queryMode : 'remote',
			selectOnFocus : true,
			listConfig: {
				emptyText: "<div style='padding:12px 8px 12px 8px; color:red;'><i class='fa fa-exclamation-circle'></i> ไม่พบรหัสลูกค้าดังกล่าวลงทะเบียน กรุณาระบุใหม่</div>",
				getInnerTpl : function() {
					return '<div> <b>{CUSTOMER_CODE}</b> : {CUSTOMER_NAME_CANDIDATE} </div>';
				}
			},
			listeners : {
				select : function(combo, records, eOpts) {
					var g = Ext.getCmp('grid_activity');
					var s = g.getStore();
					s.load({
						params: {
							fiscalYear: Ext.getCmp('text_fiscalYear').value,
							applicantId: records.data.ID
						}
					});					
					s.group("REGISTER_NUMBER");
					
				},
				change : function (oldValue, newValue) {
					
					if(newValue == null || newValue.length == 0) {
						var g = Ext.getCmp('grid_activity');
						var s = g.getStore();
						s.load({
							params: {
								fiscalYear: Ext.getCmp('text_fiscalYear').value
							}
						});
						s.group("BRANCH_NAME");
					}
				}
			
			}
		
//		},{
//			xtype      : 'fieldcontainer',
//            fieldLabel : 'ประเภท',
//            defaultType: 'radiofield',
//            defaults: {
//                width: 90
//            },
//            layout: 'hbox',
//            items: [
//                {
//                    boxLabel  : 'กิจกรรม',
//                    name      : 'activityType',
//                    inputValue: 'act',
//                    id        : 'radio1'
//                }, {
//                    boxLabel  : 'QC-Sample',
//                    name      : 'activityType',
//                    inputValue: 'qc',
//                    id        : 'radio2'
//                }, {
//                    boxLabel  : 'ทั้งหมด',
//                    name      : 'activityType',
//                    inputValue: 'all',
//                    id        : 'radio3'
//                }
//            ]
//	            
		},{
			xtype: 'checkboxfield',
			name: 'chkbx_englishReport',
			boxLabel: 'ภาษาอังกฤษ',
			fieldLabel: 'พิมพ์ที่อยู่'
		}, {
			xtype: 'grid',
			id: 'grid_activity',
			store: activityStore,
			height: 400,
			columns: [
			          {text: "สาขา", dataIndex: 'BRANCH_NAME', hidden: true },
			          {text: "ตัวอย่าง", dataIndex: 'EXAMPLE_NAME', width: 100},
			          {text: "รหัส", dataIndex: 'ACTIVITY_CODE', width: 60},
			          {text: "รายการ", dataIndex: 'ACTIVITY_NAME', width: 350, flex:1},
			          {text: "เลขที่ลงทะเบียน", dataIndex: 'REGISTER_NUMBER', hidden: true}
			],
			selModel: Ext.create('Ext.selection.CheckboxModel', {
				mode: 'MULTI',
				checkOnly: false,
				listeners: {
					selectionchange: function(rowModel, selected, eOpts) {
						
					}
				}
				
				
			}),
			features: [groupingFeature],
			frame:false,
			viewConfig: {
				emptyText: 'no data!'
			}
			
		}],

		buttons : [  {
			text: 'พิมพ์ทุกกิจกรรมยกเว้นที่เลือก',
			id: 'btn_allExceptSelected',
			listeners : {
				click : function() {
					var form = this.up('form');
					var selected = Ext.getCmp('grid_activity').getSelectionModel().getSelection();
					
					var ids=[];
					
					for(var i=0; i<selected.length;i++) {
						ids.push(selected[i].data.ID);
					}

					
					
					form.getEl().dom.target = '_blank';
					
					
					form.submit({
						target: '_blank',
						params: {
							activityId : ids,
							allExceptActivityFlag : 'true',
							reportPage: 'quotationReport'
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
					
					var selected = Ext.getCmp('grid_activity').getSelectionModel().getSelection();
					
					var ids=[];
					
					for(var i=0; i<selected.length;i++) {
						ids.push(selected[i].data.ID);
					}

					
					form.getEl().dom.target = '_blank';
					
					
					form.submit({
						target: '_blank',
						params: {
							activityId : ids,
							reportPage: 'confirm2Report'
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
					
					var selected = Ext.getCmp('grid_activity').getSelectionModel().getSelection();
					
					var ids=[];
					
					for(var i=0; i<selected.length;i++) {
						ids.push(selected[i].data.ID);
					}


					form.submit({
						target: '_blank',
						params: {
							activityId : ids,
							reportPage: 'quotationReport'
						}
					
					});
				}
			}
		} ]
	});

	//var el = simpleForm.getEl();
	//el.dom.target = '_blank';
	simpleForm.render('formCanvas');
	 
});