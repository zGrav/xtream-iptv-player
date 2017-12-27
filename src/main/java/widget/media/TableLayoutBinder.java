package widget.media;

import android.content.Context;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import com.xtream_iptv.player.R;

public class TableLayoutBinder {
    private Context mContext;
    public TableLayout mTableLayout;
    public ViewGroup mTableView;

    private static class ViewHolder {
        public TextView mNameTextView;
        public TextView mValueTextView;

        private ViewHolder() {
        }

        public void setName(String name) {
            if (this.mNameTextView != null) {
                this.mNameTextView.setText(name);
            }
        }

        public void setValue(String value) {
            if (this.mValueTextView != null) {
                this.mValueTextView.setText(value);
            }
        }
    }

    public TableLayoutBinder(Context context) {
        this(context, (int) R.layout.table_media_info);
    }

    public TableLayoutBinder(Context context, int layoutResourceId) {
        this.mContext = context;
        this.mTableView = (ViewGroup) LayoutInflater.from(this.mContext).inflate(layoutResourceId, null);
        this.mTableLayout = (TableLayout) this.mTableView.findViewById(R.id.table);
    }

    public TableLayoutBinder(Context context, TableLayout tableLayout) {
        this.mContext = context;
        this.mTableView = tableLayout;
        this.mTableLayout = tableLayout;
    }

    public View appendRow1(String name, String value) {
        return appendRow(R.layout.table_media_info_row1, name, value);
    }

    public View appendRow1(int nameId, String value) {
        return appendRow1(this.mContext.getString(nameId), value);
    }

    public View appendRow2(String name, String value) {
        return appendRow(R.layout.table_media_info_row2, name, value);
    }

    public View appendRow2(int nameId, String value) {
        return appendRow2(this.mContext.getString(nameId), value);
    }

    public View appendSection(String name) {
        return appendRow(R.layout.table_media_info_section, name, null);
    }

    public View appendSection(int nameId) {
        return appendSection(this.mContext.getString(nameId));
    }

    public View appendRow(int layoutId, String name, String value) {
        ViewGroup rowView = (ViewGroup) LayoutInflater.from(this.mContext).inflate(layoutId, this.mTableLayout, false);
        setNameValueText(rowView, name, value);
        this.mTableLayout.addView(rowView);
        return rowView;
    }

    public ViewHolder obtainViewHolder(View rowView) {
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        if (viewHolder != null) {
            return viewHolder;
        }
        viewHolder = new ViewHolder();
        viewHolder.mNameTextView = (TextView) rowView.findViewById(R.id.name);
        viewHolder.mValueTextView = (TextView) rowView.findViewById(R.id.value);
        rowView.setTag(viewHolder);
        return viewHolder;
    }

    public void setNameValueText(View rowView, String name, String value) {
        ViewHolder viewHolder = obtainViewHolder(rowView);
        viewHolder.setName(name);
        viewHolder.setValue(value);
    }

    public void setValueText(View rowView, String value) {
        obtainViewHolder(rowView).setValue(value);
    }

    public ViewGroup buildLayout() {
        return this.mTableView;
    }

    public Builder buildAlertDialogBuilder() {
        Builder dlgBuilder = new Builder(this.mContext);
        dlgBuilder.setView(buildLayout());
        return dlgBuilder;
    }
}
