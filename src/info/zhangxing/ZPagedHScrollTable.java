package info.zhangxing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 带分页功能的可滚动列数据表格
 *
 * FIXME: ProgressDialog 正在显示时若改变设备方向，会导致出错退出...目前只能在 AndroidManifest.xml 中设置
 * screen 保持特定方向(portrait/landscape)
 *
 * @author open@zhangxing.info
 *
 */
public class ZPagedHScrollTable extends FrameLayout {
	private static final float OFFSET_THRESHOLD_DP = 30.0f; // 判断上下翻页的位移阈值（单位dp）

	private float pixelPerDp;
	private int pageSize = 10; // 表格每页显示记录条数，默认为 10
	private int totalNum = 0; // 待显示总记录条数
	private int startRow = 0; // 起始行号
	private int endRow = 0; // 结束行号
	private ZPagedHScrollTableDataAdapter adapter = null; // 使用方传入的数据适配器对象

	private LayoutInflater layoutInflater;
	private ImageView upIndicator;
	private ImageView downIndicator;
	private ImageView leftIndicator;
	private ImageView rightIndicator;

	private float startX = 0.0f;
	private float startY = 0.0f;
	private float endX = 0.0f;
	private float endY = 0.0f;

	/**
	 * 获取每页展现记录数
	 *
	 * @return 当前设置的每页显示记录条数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页展现记录数
	 *
	 * @param pageSize
	 *            每页展现记录数
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 获取当前所用的表格数据适配器对象
	 *
	 * @return 表格数据适配器对象
	 */
	public ZPagedHScrollTableDataAdapter getAdapter() {
		return adapter;
	}

	/**
	 * 设置表格数据适配器对象
	 *
	 * @param adapter
	 *            待使用的表格数据适配器对象
	 */
	public void setAdapter(ZPagedHScrollTableDataAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * 强制重置并刷新表格
	 */
	public void refreshTable() {
		new InitTableStruct().execute((Void[]) null);
	}

	public ZPagedHScrollTable(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 当前设备屏幕 1 dp 对应的像素数
		pixelPerDp = getContext().getResources().getDisplayMetrics().density;

		init(context);
	}

	private void init(Context context) {
		// 从 Layout XML 文件中加载控件
		layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.zpaged_hscroll_table, this);

		upIndicator = (ImageView) findViewById(R.id.upIndicator);
		downIndicator = (ImageView) findViewById(R.id.downIndicator);
		leftIndicator = (ImageView) findViewById(R.id.leftIndicator);
		rightIndicator = (ImageView) findViewById(R.id.rightIndicator);

		// 创建时表格总是位于首页，可滚动区域位于最左边，控件刚创建时没有内容，故隐藏对应方向的指示符
		upIndicator.setVisibility(INVISIBLE);
		downIndicator.setVisibility(INVISIBLE);

		ZPagedHScrollTableHSV scrollableArea = (ZPagedHScrollTableHSV) findViewById(R.id.scrollableArea);
		scrollableArea.setLeftIndicator(leftIndicator);
		scrollableArea.setRightIndicator(rightIndicator);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		handleTouchEvent(ev);
		return false;
	}

	private class InitTableStruct extends AsyncTask<Void, Void, PageData> {
		ProgressDialog waitDiag;

		@Override
		protected void onPreExecute() {
			waitDiag = ProgressDialog.show(getContext(), "请稍等...",
					"获取数据中，请稍等...");
		}

		@Override
		protected PageData doInBackground(Void... params) {
			totalNum = adapter.getTotalRows();

			startRow = 0;
			endRow = (totalNum < pageSize ? totalNum : pageSize) - 1;

			PageData pageData = new PageData();
			pageData.headData = adapter.getColHeaders();
			pageData.rowData = adapter.getRows(startRow, endRow);
			pageData.rowOnClick = new ArrayList<OnClickListener>();

			int dataSize = pageData.rowData.size();
			for (int i = 0; i < dataSize; ++i) {
				pageData.rowOnClick.add(adapter
						.getOnClickListener(startRow + i));
			}
			endRow = startRow + dataSize - 1;

			return pageData;
		}

		@Override
		protected void onPostExecute(PageData pageData) {
			// FIXME: 若不将表格背景色设为透明，在清空表格后会出现两条竖线，需要考虑有没有在 XML layout
			// 文件中直接更改的方法...
			TableLayout fixedTable = (TableLayout) findViewById(R.id.fixedTable);
			TableLayout scrollableTable = (TableLayout) findViewById(R.id.scrollableTable);
			fixedTable.setBackgroundColor(Color.argb(0, 0, 0, 0));
			scrollableTable.setBackgroundColor(Color.argb(0, 0, 0, 0));

			setTableHead(pageData);
			setTableData(pageData);

			fixedTable
					.setBackgroundResource(R.color.zPagedHScrollTableBorderColor);
			scrollableTable
					.setBackgroundResource(R.color.zPagedHScrollTableBorderColor);

			waitDiag.dismiss();
		}

	}

	private class ShowPage extends AsyncTask<Void, Void, PageData> {
		ProgressDialog waitDiag;

		@Override
		protected void onPreExecute() {
			waitDiag = ProgressDialog.show(getContext(), "请稍候...", "获取数据中...");
		}

		@Override
		protected PageData doInBackground(Void... params) {
			PageData pageData = new PageData();
			pageData.headData = adapter.getColHeaders();
			pageData.rowData = adapter.getRows(startRow, endRow);
			pageData.rowOnClick = new ArrayList<OnClickListener>();

			int dataSize = pageData.rowData.size();
			for (int i = 0; i < dataSize; ++i) {
				pageData.rowOnClick.add(adapter
						.getOnClickListener(startRow + i));
			}
			endRow = startRow + dataSize - 1;

			return pageData;
		}

		@Override
		protected void onPostExecute(PageData result) {
			setTableData(result);
			waitDiag.dismiss();
		}

	}

	private void clearHeadRow() {
		int[] tables = { R.id.fixedTable, R.id.scrollableTable };
		for (int id : tables) {
			TableLayout table = (TableLayout) findViewById(id);
			// 清空表头
			TableRow headRow = (TableRow) table.getChildAt(0);
			headRow.removeAllViews();
		}
	}

	private void clearDataRow() {
		int[] tables = { R.id.fixedTable, R.id.scrollableTable };
		for (int id : tables) {
			TableLayout table = (TableLayout) findViewById(id);
			// 删除除表头以外的所有数据行
			ArrayList<View> rows = new ArrayList<View>();
			for (int i = 1; i < table.getChildCount(); ++i) {
				rows.add(table.getChildAt(i));
			}
			for (View row : rows) {
				table.removeView(row);
			}
		}
	}

	private void setTableHead(PageData pageData) {
		clearHeadRow();

		Map<Integer, String[]> colHeaders = pageData.headData;
		TableRow fixedColHeadRow = (TableRow) findViewById(R.id.fixedColHeadRow);
		boolean firstCol = true;
		for (String colHead : colHeaders
				.get(ZPagedHScrollTableDataAdapter.FIXED_COLUMN)) {
			// 填充固定列表头
			TextView head = (TextView) layoutInflater.inflate(
					R.layout.zpaged_hscroll_table_head_view, null);
			head.setText(colHead);

			TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
					TableRow.LayoutParams.FILL_PARENT,
					TableRow.LayoutParams.FILL_PARENT, 1.0f);

			if (firstCol) {
				firstCol = false;
				layoutParams.setMargins(0, 0, 0, 0);
			} else {
				layoutParams.setMargins(d2p(1), 0, 0, 0);
			}
			head.setLayoutParams(layoutParams);

			fixedColHeadRow.addView(head);
		}

		TableRow scrollableColHeadRow = (TableRow) findViewById(R.id.scrollableColHeadRow);
		firstCol = true;
		for (String colHead : colHeaders
				.get(ZPagedHScrollTableDataAdapter.SCROLLABLE_COLUMN)) {
			TextView head = (TextView) layoutInflater.inflate(
					R.layout.zpaged_hscroll_table_head_view, null);
			head.setText(colHead);

			TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
					TableRow.LayoutParams.FILL_PARENT,
					TableRow.LayoutParams.FILL_PARENT, 1.0f);
			if (firstCol) {
				firstCol = false;
				layoutParams.setMargins(0, 0, 0, 0);
			} else {
				layoutParams.setMargins(d2p(1), 0, 0, 0);
			}
			head.setLayoutParams(layoutParams);

			scrollableColHeadRow.addView(head);
		}
	}

	private void setTableData(PageData pageData) {
		clearDataRow();
		if (startRow > 0) {
			upIndicator.setVisibility(VISIBLE);
		} else {
			upIndicator.setVisibility(INVISIBLE);
		}
		if (endRow < totalNum - 1) {
			downIndicator.setVisibility(VISIBLE);
		} else {
			downIndicator.setVisibility(INVISIBLE);
		}

		int[] layoutIds = { R.layout.zpaged_hscroll_table_fixed_row,
				R.layout.zpaged_hscroll_table_scrollable_row };
		Integer[] colTypes = { ZPagedHScrollTableDataAdapter.FIXED_COLUMN,
				ZPagedHScrollTableDataAdapter.SCROLLABLE_COLUMN };
		TableLayout[] tables = { (TableLayout) findViewById(R.id.fixedTable),
				(TableLayout) findViewById(R.id.scrollableTable) };

		// 准备表格填充空行数据
		Map<Integer, List<String>> dummyRow = new HashMap<Integer, List<String>>();
		String[] fHead = pageData.headData
				.get(ZPagedHScrollTableDataAdapter.FIXED_COLUMN);
		String[] sHead = pageData.headData
				.get(ZPagedHScrollTableDataAdapter.SCROLLABLE_COLUMN);
		if (fHead != null) {
			dummyRow.put(ZPagedHScrollTableDataAdapter.FIXED_COLUMN,
					Arrays.asList(fHead));
		} else {
			dummyRow.put(ZPagedHScrollTableDataAdapter.FIXED_COLUMN, null);
		}
		if (sHead != null) {
			dummyRow.put(ZPagedHScrollTableDataAdapter.SCROLLABLE_COLUMN,
					Arrays.asList(sHead));
		} else {
			dummyRow.put(ZPagedHScrollTableDataAdapter.SCROLLABLE_COLUMN, null);
		}

		int actualDataSize = pageData.rowData.size();
		boolean isRowA = true;
		for (int i = 0; i < pageSize; ++i) {
			Map<Integer, List<String>> rowData = null;
			if (i < actualDataSize) {
				rowData = pageData.rowData.get(i);
			} else {
				rowData = dummyRow;
			}

			// 顺序填充固定列和滑动列数据
			for (int areaId = 0; areaId < layoutIds.length; ++areaId) {
				List<String> cols = rowData.get(colTypes[areaId]);

				TableRow rowLayout = (TableRow) layoutInflater.inflate(
						layoutIds[areaId], null);
				TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(
						TableLayout.LayoutParams.WRAP_CONTENT,
						TableLayout.LayoutParams.FILL_PARENT, 1.0f);
				if (colTypes[areaId] == ZPagedHScrollTableDataAdapter.FIXED_COLUMN) {
					// 固定列数据行布局属性
					rowLayoutParams.setMargins(d2p(1), 0, d2p(1), d2p(1));
				} else {
					// 滑动列数据行布局属性
					rowLayoutParams.setMargins(0, 0, d2p(1), d2p(1));

				}
				rowLayout.setLayoutParams(rowLayoutParams);

				boolean firstCol = true;
				for (String col : cols) {
					TextView cell = null;

					// 以不同的配色分隔相邻行
					if (isRowA) {
						cell = (TextView) layoutInflater.inflate(
								R.layout.zpaged_hscroll_table_col_view_a, null);
					} else {
						cell = (TextView) layoutInflater.inflate(
								R.layout.zpaged_hscroll_table_col_view_b, null);
					}
					cell.setText(col);
					// XXX: 让超出列宽的文本开始走马灯动画展示
					cell.setSelected(true);
					// 隐藏超过有效数据行范围的填充文本
					if (i >= actualDataSize) {
						cell.setTextColor(Color.argb(0, 0, 0, 0));
					}

					TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
							TableRow.LayoutParams.WRAP_CONTENT,
							TableRow.LayoutParams.FILL_PARENT, 1.0f);
					if (firstCol) {
						firstCol = false;
						layoutParams.setMargins(0, 0, 0, 0);
					} else {
						layoutParams.setMargins(d2p(1), 0, 0, 0);
					}
					cell.setLayoutParams(layoutParams);

					rowLayout.addView(cell);
				}

				// 为有效数据行范围内的行添加点击处理器
				if (i < actualDataSize) {
					rowLayout.setOnClickListener(pageData.rowOnClick.get(i));
				} else {
					// XXX: 空白区域的 TableRow 也必须有 OnClickListener，才能正常识别固定列向上翻页动作
					rowLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					});
				}

				tables[areaId].addView(rowLayout);
			}

			// 切换行背景配色
			isRowA = !isRowA;
		}
	}

	private void handleTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = endX = event.getX();
			startY = endY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			// Y 轴滑动速度高于阈值时触发上下翻页操作
			endX = event.getX();
			endY = event.getY();
			if (Math.abs(startX - endX) < Math.abs(startY - endY)) {
				// X 轴位移没有 Y 轴位移大，若 Y 轴位移达到阈值则触发上下翻页操作
				float offY = endY - startY;
				if (offY > d2p(OFFSET_THRESHOLD_DP)) {
					doPageUp();
				} else if (offY < -d2p(OFFSET_THRESHOLD_DP)) {
					doPageDown();
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
	}

	private void doPageDown() {
		if (endRow >= 0 && endRow < totalNum - 1 && pageSize > 0) {
			startRow = endRow + 1;
			endRow += pageSize;
			if (endRow > totalNum - 1) {
				endRow = totalNum - 1;
			}
			new ShowPage().execute((Void[]) null);
		} else {
			String msg = "已经是最后一页";
			Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	private void doPageUp() {
		if (startRow >= pageSize && pageSize > 0) {
			endRow = startRow - 1;
			startRow -= pageSize;
			new ShowPage().execute((Void[]) null);
		} else {
			String msg = "已经是第一页";
			Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	private int d2p(float dp) {
		return (int) (dp * pixelPerDp + 0.5);
	}

	private class PageData {
		public Map<Integer, String[]> headData;
		public List<Map<Integer, List<String>>> rowData;
		public List<OnClickListener> rowOnClick;
	}

}
