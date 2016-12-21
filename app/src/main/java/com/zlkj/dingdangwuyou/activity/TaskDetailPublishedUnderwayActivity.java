package com.zlkj.dingdangwuyou.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zlkj.dingdangwuyou.R;
import com.zlkj.dingdangwuyou.adapter.TaskImageAdapter;
import com.zlkj.dingdangwuyou.base.BaseActivity;
import com.zlkj.dingdangwuyou.entity.Receiver;
import com.zlkj.dingdangwuyou.entity.Task;
import com.zlkj.dingdangwuyou.entity.TaskTypeList;
import com.zlkj.dingdangwuyou.net.MyHttpClient;
import com.zlkj.dingdangwuyou.net.Url;
import com.zlkj.dingdangwuyou.utils.AppTool;
import com.zlkj.dingdangwuyou.utils.Const;
import com.zlkj.dingdangwuyou.utils.GsonUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 进行中的任务详情(我发布的)
 * Created by btx on 2016/11/25.
 */

public class TaskDetailPublishedUnderwayActivity extends BaseActivity {

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.txtType)
    TextView txtType;
    @BindView(R.id.txtName)
    EditText txtName;
    @BindView(R.id.txtContent)
    EditText txtContent;
    @BindView(R.id.txtFinishTime)
    TextView txtFinishTime;
    @BindView(R.id.txtIdentity)
    EditText txtIdentity;
    @BindView(R.id.txtArea)
    EditText txtArea;
    @BindView(R.id.txtCount)
    EditText txtCount;
    @BindView(R.id.txtPacketMoney)
    EditText txtPacketMoney;
    @BindView(R.id.txtPacketNum)
    EditText txtPacketNum;
    @BindView(R.id.txtPhone)
    EditText txtPhone;
    @BindView(R.id.txtMessage)
    EditText txtMessage;
    @BindView(R.id.gridViImg)
    GridView gridViImg;

    @BindView(R.id.txtHandle)
    TextView txtHandle;
    @BindView(R.id.txtReceiver)
    TextView txtReceiver;

    private Task task;
    private List<String> imgList;
    private TaskImageAdapter imgAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_task_detail_published_underway;
    }

    @Override
    protected void initData() {
        txtTitle.setText("任务详情");

        task = (Task) getIntent().getSerializableExtra(Const.KEY_OBJECT);
        setList();
        setData();
    }

    private void setList() {
        imgList = new ArrayList<String>();
        imgAdapter = new TaskImageAdapter(context, imgList);
        gridViImg.setAdapter(imgAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getReceiver();
    }

    /**
     * 渲染数据
     */
    private void setData() {
        txtType.setText(getTaskType());
        txtName.setText(task.getT_name());
        txtContent.setText(task.getT_content());
        txtFinishTime.setText(AppTool.dateFormat(task.getT_finish_time().getTime(), "yyyy-MM-dd"));
        txtIdentity.setText(task.getT_status());
        txtArea.setText(task.getT_area());
        txtCount.setText(task.getT_num());
        txtPacketMoney.setText(task.getT_money() + "     (单位:元)");
        txtPacketNum.setText(task.getT_hbnum());
        txtPhone.setText(task.getT_contact());
        txtMessage.setText(task.getT_words());

        if (!TextUtils.isEmpty(task.getPicture())) {
            String[] data = task.getPicture().split(",");
            imgList.clear();
            imgList.addAll(Arrays.asList(data));
            imgAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取接令人列表
     */
    private void getReceiver() {
        RequestParams params = new RequestParams();
        params.put("id", task.getId());
        params.put("page", 1);
        params.put("pageSize", 500);

        MyHttpClient.getInstance().post(Url.URL_TASK_GET_JIELING_ID, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray jsonArr = jsonObj.getJSONArray("items");
                    List<Receiver> list = GsonUtil.getEntityList(jsonArr.toString(), Receiver.class);
                    int chooseNum = getChooseReceiverNum(list);
                    txtReceiver.setText("接令人 (已选" + chooseNum + ")");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    /**
     * 根据任务类型id获取类型名称
     * @return
     */
    private String getTaskType() {
        String result = "";
        for (TaskTypeList.TaskType taskType : TaskTypeList.list) {
            if (taskType.getId().equals(task.getCa_id())) {
                result = taskType.getName();
                break;
            }
        }

        return result;
    }

    /**
     * 获取已选择的接令人数量
     * @param list
     * @return
     */
    private int getChooseReceiverNum(List<Receiver> list) {
        int result = 0;
        for (Receiver receiver : list) {
            int status = Integer.valueOf(receiver.getJltai());
            if (status == Const.JIELING_STATUS_UNDERWAY) {
                result++;
            }
        }

        return result;
    }


    @OnClick({R.id.imgViBack, R.id.txtHandle, R.id.txtReceiver})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.imgViBack: //返回
                finish();
                break;
            case R.id.txtHandle: //任务处理

                break;
            case R.id.txtReceiver: //选择接令人
                intent = new Intent(context, ChooseReceiverActivity.class);
                intent.putExtra(Const.KEY_OBJECT, task);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_to_top, R.anim.no_change);
                break;
            default:
                break;
        }
    }


}
