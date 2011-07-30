package info.zhangxing;

import java.util.List;
import java.util.Map;

import android.view.View.OnClickListener;

/**
 * 数据适配器接口，用于获取待展现的数据内容
 *
 * @author open@zhangxing.info
 *
 */
public interface ZPagedHScrollTableDataAdapter {
	final static Integer FIXED_COLUMN = 0;
	final static Integer SCROLLABLE_COLUMN = 1;

	/**
	 * 获取列标题列表
	 *
	 * @return 一个 HashMap，其中 FIXED_COLUMN 为键的列表是固定列标题数据；SCROLLABLE_COLUMN
	 *         为键的列表是滑动列数据。两个键必须都存在，对应列类型没有需要展现的列时应取值空数组。
	 */
	Map<Integer, String[]> getColHeaders();

	/**
	 * 获取总数据行数
	 *
	 * @return 总数据行数
	 */
	int getTotalRows();

	/**
	 * 获取指定范围内的记录数据
	 *
	 * @param startRow
	 *            起始行号，从 0 开始计算
	 * @param endRow
	 *            结束行号，从 0 开始计算
	 * @return 包含若干行记录的列表，每行记录是一个 HashMap，其中 FIXED_COLUMN
	 *         为键的列表是固定列数据，顺序同固定列标题的顺序；SCROLLABLE_COLUMN
	 *         为键的列表是滑动列数据，顺序同滑动列标题的顺序。若数据少于要求的行号范围，则仅返回实际数量的数据。无数据时应返回一个空 List。
	 */
	List<Map<Integer, List<String>>> getRows(final int startRow,
			final int endRow);

	/**
	 * 获取指定行对应的点击事件处理对象
	 *
	 * @param row
	 *            在整个数据集中的行号，并非当前页内的行号，从 0 开始计算
	 * @return 点击事件处理对象，不需要处理点击事件时应返回 null
	 */
	OnClickListener getOnClickListener(final int row);
}
