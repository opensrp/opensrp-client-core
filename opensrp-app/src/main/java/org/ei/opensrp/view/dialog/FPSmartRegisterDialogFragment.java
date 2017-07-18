package org.ei.opensrp.view.dialog;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.ei.opensrp.R;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;

public class FPSmartRegisterDialogFragment extends DialogFragment {
    private final SecuredNativeSmartRegisterActivity parentActivity;
    private final DialogOption[] options;
    private final DialogOption[] prioritizationOptions;
    private final DialogOptionModel dialogOptionModel;
    private final Object tag;
    private ListView listView;
    private onSelectedListener selectedListener;

    private FPSmartRegisterDialogFragment(SecuredNativeSmartRegisterActivity activity,
                                          DialogOptionModel dialogOptionModel,
                                          Object tag) {
        this.parentActivity = activity;
        this.options = dialogOptionModel.getDialogOptions();
        this.prioritizationOptions = ((FPDialogOptionModel) dialogOptionModel).getPrioritizationDialogOptions();
        this.dialogOptionModel = dialogOptionModel;
        this.tag = tag;
    }

    public static FPSmartRegisterDialogFragment newInstance(
            SecuredNativeSmartRegisterActivity activity,
            DialogOptionModel dialogOptionModel, Object tag) {
        return new FPSmartRegisterDialogFragment(activity, dialogOptionModel, tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.fp_smart_register_dialog_view, container, false);
        listView = (ListView) dialogView.findViewById(R.id.dialog_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();
                dialogOptionModel.onDialogOptionSelection(((DialogOption) adapterView.getItemAtPosition(i)), tag);
            }
        });

        return dialogView;
    }

    private ArrayAdapter<DialogOption> getDialogOptionArrayAdapter(DialogOption[] options) {
        final LayoutInflater inflater = LayoutInflater.from(parentActivity);
        return new ArrayAdapter<DialogOption>(
                    parentActivity, R.layout.smart_register_dialog_list_item, options) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewGroup itemView;
                    if (convertView == null) {
                        itemView = (ViewGroup) inflater.inflate(R.layout.smart_register_dialog_list_item, parent, false);
                    } else {
                        itemView = (ViewGroup) convertView;
                    }

                    ((TextView) itemView.findViewById(R.id.dialog_list_option))
                            .setText(getItem(position).name());
                    return itemView;
                }
            };
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        RadioGroup rgTab = (RadioGroup) view.findViewById(R.id.rg_fp_tab);
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                ArrayAdapter<DialogOption> adapter = null;
                if (checkedId == R.id.rb_fp_method) {
                    dispatchEvent(R.id.rb_fp_method);
                    adapter = getDialogOptionArrayAdapter(options);

                } else if (checkedId == R.id.rb_fp_prioritization) {
                    dispatchEvent(R.id.rb_fp_prioritization);
                    adapter = getDialogOptionArrayAdapter(prioritizationOptions);

                }

                listView.setAdapter(adapter);
            }
        });

        rgTab.check(getCurrentTabSelection());
    }

    private int getCurrentTabSelection() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("FP_DIALOG_TAB_SELECTION")
                && bundle.getInt("FP_DIALOG_TAB_SELECTION") != 0) {
            return bundle.getInt("FP_DIALOG_TAB_SELECTION");
        }
        return R.id.rb_fp_method;
    }

    public DialogFragment setSelectedListener(onSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
        return this;
    }

    private void dispatchEvent(int id) {
        if (selectedListener != null) {
            selectedListener.onSelected(id);
        }
    }

    public interface onSelectedListener {
        public void onSelected(int id);
    }
}
