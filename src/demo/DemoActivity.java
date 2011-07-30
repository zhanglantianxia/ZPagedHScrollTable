package demo;

import info.zhangxing.R;
import info.zhangxing.ZPagedHScrollTable;
import info.zhangxing.ZPagedHScrollTableDataAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class DemoActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);

		ZPagedHScrollTable pht = (ZPagedHScrollTable) findViewById(R.id.pagedHScrollTable1);
		pht.setPageSize(10); // 设置每页展现记录数
		pht.setAdapter(new DataAdaptor());

		// 刷新表格，展现数据
		pht.refreshTable();
	}

	class DataAdaptor implements ZPagedHScrollTableDataAdapter {

		Map<Integer, String[]> colHeaders;

		{
			colHeaders = new HashMap<Integer, String[]>();
			colHeaders.put(ZPagedHScrollTableDataAdapter.FIXED_COLUMN,
					new String[] { "固定列A", "固定列B" });
			colHeaders.put(ZPagedHScrollTableDataAdapter.SCROLLABLE_COLUMN,
					new String[] { "滑动列A1", "滑动列B1", "滑动列C1", "滑动列A2", "滑动列B2",
							"滑动列C2", "滑动列A3", "滑动列B3", "滑动列C3" });
		}

		@Override
		public Map<Integer, String[]> getColHeaders() {
			return colHeaders;
		}

		@Override
		public int getTotalRows() {
			// 整个数据集共30行
			return 30;
		}

		@Override
		public List<Map<Integer, List<String>>> getRows(int startRow, int endRow) {
			List<Map<Integer, List<String>>> rows = new ArrayList<Map<Integer, List<String>>>();
			for (int i = startRow; i <= endRow; ++i) {
				Map<Integer, List<String>> row = new HashMap<Integer, List<String>>();

				List<String> fCols = new ArrayList<String>();
				fCols.add("固定列A数据" + i);
				fCols.add("固定列B数据" + i);
				row.put(ZPagedHScrollTableDataAdapter.FIXED_COLUMN, fCols);

				List<String> sCols = new ArrayList<String>();
				for (int j = 0; j < 9; ++j) {
					sCols.add("滑动列" + j + "数据" + i);
				}
				row.put(ZPagedHScrollTableDataAdapter.SCROLLABLE_COLUMN, sCols);

				rows.add(row);
			}

			// 模拟数据获取延迟
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}

			return rows;
		}

		@Override
		public OnClickListener getOnClickListener(final int row) {
			return new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(DemoActivity.this, "你点击了第" + row + "行",
							Toast.LENGTH_SHORT).show();
				}
			};
		}

	}
}
