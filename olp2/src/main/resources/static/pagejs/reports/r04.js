	
var currentFiscalYear;
Ext.onReady(function(){
	Ext.QuickTips.init();
	
	
	Ext.define('PlanActivity', {
		extend: 'Ext.data.Model',
		fields : [{name:'EXAMPLE_ID'},
		          {name:'ACTIVITY_ID'},
		          {name:'PLAN_ID'},
		          {name:'PLAN_CODE'},
		          {name:'BRANCH_NAME'},
		          {name:'EXAMPLE_CODE'},
		          {name:'EXAMPLE_NAME'},
		          {name:'ACTIVITY_CODE'},
		          {name:'ACTIVITY_NAME'},
		          {name:'PRICE', type: 'number'},
		          {name:'ROOM_NUMBER'},
		          {name:'CLOSE_APPLICANT_DATE', type: 'date', dateFormat: 'c'},
		          {name:'START_ACTIVITY_DATE', type: 'date', dateFormat: 'c'},
		          {name:'EMP_NAME'},
		          {name:'ROUND'}]
	});
	
	Ext.define('Customer', {
		extend : 'Ext.data.Model',
		fields : ['ID', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE']
	});
	
	Ext.define('Register', {
		extend : 'Ext.data.Model',
		fields : ['ID', 'REGISTER_NUMBER', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE', 'ACTIVITIES']
	});


	
	var planActivityStore = new Ext.data.Store({
		model: 'PlanActivity',
		id: "store_planActivityStore",
		groupField: 'PLAN_ID',
		proxy: {
			type: 'ajax',
			url: appUrl('/json/listPlanActivity')
		}
	});
	
	var customerStore = new Ext.data.Store({
		model : 'Customer',
		id : 'store_customerStore',
		proxy : {
			type : 'ajax',
			url : appUrl('/json/listRegisterByActivtiy')
		}
	});

	var simpleForm = Ext.create('Ext.form.Panel', {
		id : 'mainForm',
		url : 'exportCustomerRegistration',
		frame : true,
		title : 'ข้อมูลการลงทะเบียนกิจกรรม',
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
			listeners: {
				change: function(field, newValue, oldValue, eOpts) {
					//now set url for customer Store
					Ext.getStore('store_planActivityStore').load({
						params: {
							fiscalYear: newValue
						}
					});
					currentFiscalYear = newValue;
					
				}
			}
		}, {
			fieldLabel : 'กิจกรรม',
			name : 'activityCombo',
			id : 'combo_activityCombo',
			xtype : 'combo',
			displayField: 'ACTIVITY_CODE',
			store : planActivityStore,
			valueField : 'ACTIVITY_ID',
			typeAhead : true,
			valueNotFoundText : "ไม่พบรหัสกิจกรรมนี้",
			minChars : 2,
			queryMode : 'local',
			listConfig: {
				emptyText: "<div style='padding:12px 8px 12px 8px; color:red;'><i class='fa fa-exclamation-circle'></i> ไม่พบรหัสกิจกรรมนี้ กรุณาระบุใหม่</div>",
//				getInnerTpl : function() {
//					return '<div> <b>{ACTIVITY_CODE}</b> : {ACTIVITY_NAME} </div>';
//				}
			},
	        tpl: '<ul class="x-list-plain">' +
		            '{% var lastBranch, branch; %}' +
		            '<tpl for=".">' +
		            	'{% branch = values.EXAMPLE_NAME;' +
		                // Only show region headers when there are more than 1 choices
		            		'if (branch !== lastBranch) {' + 
		                    	'lastBranch = branch;%}' +
		                    	'<li class="x-grid-group-hd x-grid-group-title">{EXAMPLE_NAME}</li>' +
		                '{%}%}'+
		                '<li class="x-boundlist-item">' +
		                    '<b>{ACTIVITY_CODE}</b> : {ACTIVITY_NAME}' +
		                '</li>'+
		            '</tpl>'+
		        '</ul>',
				
			listeners : {
				select : function(combo, records, eOpts) {
					var g = Ext.getCmp('grid_activity');
					var s = g.getStore();
					console.log(records);
					s.load({
						params: {
							fiscalYear: Ext.getCmp('text_fiscalYear').value,
							activityId: records.data.ACTIVITY_ID
						}
					});					
					
				}
//			,
//				change : function (oldValue, newValue) {
//					
//					if(newValue == null || newValue.length == 0) {
//						var g = Ext.getCmp('grid_activity');
//						var s = g.getStore();
//						s.load({
//							params: {
//								fiscalYear: Ext.getCmp('text_fiscalYear').value
//							}
//						});
//						s.group("BRANCH_NAME");
//					}
//				}
			
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
		}, {
			xtype: 'grid',
			id: 'grid_activity',
			store: customerStore,
			height: 300,
			columns: [
				 	{text: "เลขที่ลงทะเบียน", dataIndex: 'REGISTER_NUMBER', width: 100},	
				 	{text: "รหัส", dataIndex: 'CUSTOMER_CODE', width: 100 },
				 	{text: "ขื่อ", dataIndex: 'CUSTOMER_NAME_CANDIDATE', flex:1},
				 	{text: "สถานะ", dataIndex: 'STATUS_REGIS_FORM', width: 60 },
			        {text: "รหัสลับ", dataIndex: 'C_PASSWORD', width: 60 }
			          
			         
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
		},{
			text : 'ยกเลิก'
		} ]
	});

	//var el = simpleForm.getEl();
	//el.dom.target = '_blank';
	simpleForm.render('formCanvas');
	 
});