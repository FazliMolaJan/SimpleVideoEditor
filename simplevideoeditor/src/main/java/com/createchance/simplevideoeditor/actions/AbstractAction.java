package com.createchance.simplevideoeditor.actions;

import com.createchance.simplevideoeditor.VideoEditorManager;

import java.io.File;

/**
 * 音视频编辑抽象类，所有的编辑操作都是这个类的子类
 *
 * @author gaochao1-iri
 * @date 25/03/2018
 */

public abstract class AbstractAction {

    private AbstractAction mSuccessNext;

    private final String mActionName;

    protected File mInputFile;
    protected File mOutputFile;

    AbstractAction(String actionName) {
        this.mActionName = actionName;
    }

    /**
     * start this action.
     */
    public void start(File inputFile) {
        this.mInputFile = inputFile;
        this.mOutputFile = genOutputFile();
    }

    public void release() {
        if (mOutputFile != null && mOutputFile.exists()) {
            mOutputFile.delete();
        }
    }

    public final void successNext(AbstractAction action) {
        mSuccessNext = action;
    }

    protected final void onStarted() {
        VideoEditorManager.getManager().onStart(mActionName);
    }

    protected final void onProgress(float progress) {
        VideoEditorManager.getManager().onProgress(mActionName, progress);
    }

    protected final void onSucceeded() {
        // Our output file is the input of next action.
        if (mSuccessNext != null) {
            VideoEditorManager.getManager().onSucceed(mActionName);
            mSuccessNext.start(mOutputFile);
        } else {
            // We are the next action, rename our output to dest file.
            // TODO: What if renameTo return false??
            mOutputFile.renameTo(VideoEditorManager.getManager().getOutputFile());
            VideoEditorManager.getManager().onSucceed(mActionName);
            VideoEditorManager.getManager().onAllSucceed();
        }
    }

    protected final void onFailed() {
        VideoEditorManager.getManager().onFailed(mActionName);
    }

    protected final File getBaseWorkFolder() {
        return VideoEditorManager.getManager().getBaseWorkFolder();
    }

    private File genOutputFile() {
        return new File(VideoEditorManager.getManager().getBaseWorkFolder(), mActionName + ".tmp");
    }
}