package myInputFormat;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.CombineFileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class MyRecordReader extends RecordReader<Text, Text> {

	private CombineFileSplit combineFileSplit; // 当前处理的分片
	private int totalLength;                   // 分片包含的文件数量
	private int currentIndex;                  // 当前处理的文件索引
	private float currentProgress = 0;         // 当前的进度
	private Text currentKey = new Text();      // 当前的Key
	private Text currentValue = new Text();    // 当前的Value
	private Configuration conf;                // 任务信息
	private boolean processed;                 // 记录当前文件是否已经读取

	public MyRecordReader(CombineFileSplit combineFileSplit,
			TaskAttemptContext context, Integer index) throws IOException {
		super();
		this.currentIndex = index;
		this.combineFileSplit = combineFileSplit;
		conf = context.getConfiguration();
		totalLength = combineFileSplit.getPaths().length;
		processed = false;
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return currentKey;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return currentValue;
	}

	@Override
	public float getProgress() throws IOException {
		if (currentIndex >= 0 && currentIndex < totalLength) {
			currentProgress = (float) currentIndex / totalLength;
			return currentProgress;
		}
		return currentProgress;
	}

	@Override
	public void close() throws IOException {
	}
	
	@Override
	public boolean nextKeyValue() throws IOException {
		if (!processed) {    // 如果文件未处理则读取文件并设置key-value
			// set key
			Path file = combineFileSplit.getPath(currentIndex);
			currentKey.set(file.getParent().getName()); // category's name
			// set value
			FSDataInputStream in = null;
			byte[] contents = new byte[(int)combineFileSplit.getLength(currentIndex)];
			try {
				FileSystem fs = file.getFileSystem(conf);
				in = fs.open(file);
				in.readFully(contents);
				currentValue.set(contents);
			} catch (Exception e) {
			} finally {
				in.close();
			}
			processed = true;
			return true;
		}
		return false;        //如果文件已经处理，必须返回false
	}
	
}