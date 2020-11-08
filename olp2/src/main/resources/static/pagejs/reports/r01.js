var e1;

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
	
	var planActivityStore = new Ext.data.Store({
		model: 'PlanActivity',
		id: "store_planActivityStore",
		groupField: 'PLAN_ID',
		proxy: {
			type: 'ajax',
			url: appUrl('json/listPlanActivity')
		}
	});
	
	
	var groupingFeature = Ext.create('Ext.grid.feature.Grouping',{
        groupHeaderTpl: 'สาขา: {[values.rows[0].data.BRANCH_NAME]} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
    });
	
	
	var simpleForm = Ext.create('Ext.form.Panel', {
		id : 'mainForm',
		url : './reportPlanActivity',
		frame : true,
		title : 'พิมพ์รายชื่อกิจกรรมประจำปี',
		bodyStyle : 'padding:5px 5px 0',
		standardSubmit: true,
		width: 800,
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
			listeners: {
				change: function(field, newValue, oldValue, eOpts) {
					loadGrid(newValue);
				}
			}
		}, {
			xtype: 'grid',
			id: 'grid_planActivity',
			store: planActivityStore,
			height: 400,
			features: [groupingFeature],
			autoWidth:true,
			columns: [
			          {text: "ตัวอย่าง", dataIndex: 'EXAMPLE_NAME', width: 100},
			          {
			        	  text: "กิจกรรม", dataIndex: 'ACTIVITY_NAME', flex: 1, cellWrap: true,
			        	  renderer: function(value, metaData, record, rowIndex, colIndex, store, view  ) {
			        		  var roundTxt = "";
			        	  
			        		  if(record.data.ROUND != null) {
			        			  roundTxt = "(Round " + record.data.ROUND + ")";
			        		  }
			        		  
			        		  return value + roundTxt;
			        	  }
			          },
			        	
			          {text: "ราคา", dataIndex: 'PRICE', width: 60,  xtype: 'numbercolumn', format:'0,000', align: 'center'},
			          {text: "จำนวนห้อง", dataIndex: 'ROOM_NUMBER', width: 60, align: 'center'},
			          {text: "วันที่ปิดรับสมัคร", dataIndex: 'CLOSE_APPLICANT_DATE', width: 100, renderer : Ext.util.Format.dateRenderer('d M Y')},
			          {text: "วันที่เริ่มกิจกรรม", dataIndex: 'START_ACTIVITY_DATE', width: 100, renderer : Ext.util.Format.dateRenderer('d M Y')},
			          {text: "ผู้ประสานงาน", dataIndex: 'EMP_NAME', width: 120}
			],			
			frame:false,
			viewConfig: {
				emptyText: 'no data!'
			}
			
		}],

		buttons : [  {
			text : 'Excel Export',
			id : 'btn_excelExport',
			listeners : {
				click : function() {
					var form = this.up('form');
					var fiscalYear = Ext.getCmp('text_fiscalYear').getValue();
				
					
					form.getEl().dom.target = '_blank';
					
					
					form.submit({
						target: '_blank',
						params: {
							fiscalYear : fiscalYear,
							reportPage: 'excelReport'
						}
					});
				}
					
			}

		}, {
			text : 'PDF Export',
			id : 'btn_pdfExport',
			listeners : {
				click : function() {
					var form = this.up('form');
					e1 = this;
					var fiscalYear = Ext.getCmp('text_fiscalYear').getValue();
					form.getEl().dom.target = '_blank';
					form.submit({
						target: '_blank',
						params: {
							fiscalYear : fiscalYear,
							reportPage: 'pdfReport'
						}
					});
				}
					
			}
		} ]
	});

	function loadGrid(fiscalYear, firstRegister, lastRegister) {
		var grid = Ext.getCmp('grid_planActivity');
		grid.getStore().load({
			params: {
				fiscalYear: fiscalYear
			}
		});
		
	}
	
	//var el = simpleForm.getEl();
	//el.dom.target = '_blank';
	simpleForm.render('formCanvas');
	 
});