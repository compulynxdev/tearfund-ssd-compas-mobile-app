package com.compastbc.ui.cardformat;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.compastbc.R;
import com.compastbc.core.base.BasePresenter;
import com.compastbc.core.data.DataManager;
import com.compastbc.core.data.db.model.Beneficiary;
import com.compastbc.core.data.db.model.BeneficiaryDao;
import com.compastbc.core.utils.AppConstants;
import com.compastbc.core.utils.AppLogger;
import com.compastbc.core.utils.AppUtils;
import com.compastbc.nfcprint.nfc.NFCListener;
import com.compastbc.nfcprint.nfc.NFCReadDataListener;
import com.compastbc.nfcprint.nfc.NFCReader;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardFormatPresenter<V extends CardFormatMvpView> extends BasePresenter<V>
        implements CardFormatMvpPresenter<V> {

    private final NFCReader nfcReader;

    CardFormatPresenter(Activity activity, DataManager dataManager) {
        super(dataManager);
        nfcReader = NFCReader.getInstance(activity);
    }

    @Override
    public void formatCard(Intent intent, boolean isCleanFormat) {
        MaterialDialog dialog = getMvpView().materialDialog(R.string.card_format, R.string.card_remove);
        nfcReader.setIntent(intent);

        readPersonalDetails(dialog, isCleanFormat);
    }

    private void readPersonalDetails(MaterialDialog dialog, boolean isCleanFormat) {
        nfcReader.doReadCardData(NFCReader.PERSONAL_DETAIL, new NFCReadDataListener() {
            @Override
            public void onSuccess(String data) {
                if (getMvpView().isNetworkConnected()) {
                    if (data != null && !data.isEmpty()) {
                        JSONObject detail = null;
                        try {
                            detail = new JSONObject(data);
                            Beneficiary beneficiary = getDataManager().getDaoSession().getBeneficiaryDao().queryBuilder().where(BeneficiaryDao.Properties.IdentityNo.eq(detail.getString("rationalNo"))).unique();
                            if (beneficiary != null) {
                                beneficiary.setActivated(false);
                                beneficiary.setIsUploaded("0");
                                getDataManager().getDaoSession().getBeneficiaryDao().insertOrReplace(beneficiary);

                                if (isCleanFormat) {
                                    fullFormat(dialog);
                                } else {
                                    partialFormat(dialog);
                                }

                            } else {
                                dialog.dismiss();
                                getMvpView().showMessage(R.string.error, R.string.this_beneficiary_not_exist);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        dialog.dismiss();
                        getMvpView().showMessage(R.string.error, R.string.cardNotActivated);
                    }
                }
            }

            @Override
            public void onFail(String TAG, String msg) {

            }
        }, true);
    }

    private void fullFormat(MaterialDialog dialog) {
        nfcReader.doReadCardData(NFCReader.PERSONAL_DETAIL, new NFCReadDataListener() {
            @Override
            public void onSuccess(String data) {
                if (getMvpView().isNetworkConnected()) {
                    if (data != null && !data.isEmpty()) {
                        JSONObject detail = null;
                        try {
                            detail = new JSONObject(data);
                            Beneficiary beneficiary = getDataManager().getDaoSession().getBeneficiaryDao().queryBuilder().where(BeneficiaryDao.Properties.IdentityNo.eq(detail.getString("rationalNo"))).unique();
                            if (beneficiary != null) {
                                nfcReader.doFormat(new NFCListener() {
                                    @Override
                                    public void onSuccess(int flag) {
                                        dialog.dismiss();
                                        if (flag == 0) {
                                            beneficiary.setActivated(false);
                                            beneficiary.setIsUploaded("0");
                                            getDataManager().getDaoSession().getBeneficiaryDao().insertOrReplace(beneficiary);

                                            getMvpView().sweetAlert(SweetAlertDialog.SUCCESS_TYPE, R.string.great, R.string.card_format_success)
                                                                    .setConfirmClickListener(sweetAlertDialog -> getMvpView().onFormatSuccess())
                                                                    .show();
//                                            getMvpView().getAccessToken(() -> {
//                                                JSONObject object = new JSONObject();
//                                                try {
//                                                    object.put("beneficiaryId", beneficiary.getBeneficiaryId());
//                                                    object.put("rationNo", beneficiary.getIdentityNo());
//                                                    object.put("agentId", getDataManager().getUserDetail().getAgentId());
//                                                } catch (Exception e) {
//                                                    e.printStackTrace();
//                                                }
//                                                RequestBody body = AppUtils.createBody(AppConstants.CONTENT_TYPE_JSON, object.toString());
//                                                getDataManager().createFormatLog("bearer " + getDataManager().getConfigurationDetail().getAccess_token(), body).enqueue(new Callback<ResponseBody>() {
//                                                    @Override
//                                                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
//                                                        if (response.code() == 200) {
//                                                            getMvpView().sweetAlert(SweetAlertDialog.SUCCESS_TYPE, R.string.great, R.string.card_format_success)
//                                                                    .setConfirmClickListener(sweetAlertDialog -> getMvpView().onFormatSuccess())
//                                                                    .show();
//                                                        } else if (response.code() == 401) {
//                                                            getMvpView().hideLoading();
//                                                            getMvpView().openActivityOnTokenExpire();
//                                                        } else {
//                                                            getMvpView().hideLoading();
//                                                            handleApiError(response.errorBody().toString());
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                                                        getMvpView().hideLoading();
//                                                        handleApiFailure(call, t);
//                                                    }
//                                                });
//                                            });
                                        } else {
                                            getMvpView().showMessage(R.string.error, R.string.card_error_format);
                                        }
                                    }

                                    @Override
                                    public void onFail(String TAG, String msg) {
                                        dialog.dismiss();
                                    }
                                }, false);

                            } else {
                                dialog.dismiss();
                                getMvpView().showMessage(R.string.error, R.string.this_beneficiary_not_exist);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dialog.dismiss();
                        getMvpView().showMessage(R.string.error, R.string.cardNotActivated);
                    }
                }
            }

            @Override
            public void onFail(String TAG, String msg) {
                getMvpView().hideLoading();
            }
        }, true);
    }

    private void partialFormat(MaterialDialog dialog) {
        nfcReader.doReadCardDataForActivate(NFCReader.CARD_DATA, new NFCReadDataListener() {
            @Override
            public void onSuccess(String data) {
                JSONObject dataObj = null;
                try {
                    dataObj = new JSONObject(data);
                    AppLogger.d("FormatTest", "Card Data:- " + dataObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //data empty format card else delete other data except txn detail
                if (dataObj == null || dataObj.length() == 0 || data.isEmpty()) {
                    fullFormat(dialog);
                } else {
                    deleteFiles(dialog);
                }
            }

            @Override
            public void onFail(String TAG, String msg) {
                dialog.dismiss();
            }
        }, true);
    }

    private void deleteFiles(MaterialDialog dialog) {
        nfcReader.doDeleteFile(new NFCListener() {
            @Override
            public void onSuccess(int flag) {
                dialog.dismiss();
                if (flag == 0) {
                    getMvpView().sweetAlert(SweetAlertDialog.SUCCESS_TYPE, R.string.great, R.string.card_format_success)
                            .setConfirmClickListener(sweetAlertDialog -> getMvpView().onFormatSuccess())
                            .show();
                } else {
                    getMvpView().showMessage(R.string.error, R.string.card_error_format);
                }
            }

            @Override
            public void onFail(String TAG, String msg) {
                dialog.dismiss();
            }
        }, false, NFCReader.PERSONAL_DETAIL, NFCReader.CARD_PIN);
    }
}
