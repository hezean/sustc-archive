import cv2
from train import processFiles, trainSVM
from detector import Detector

# Replace these with the directories containing your
# positive and negative sample images, respectively.
pos_dir = "samples/vehicles"
neg_dir = "samples/non-vehicles"

# Replace this with the path to your test video file.
video_file = "videos/test_video.mp4"


def experiment1():
    """
    Train a classifier and run it on a video using default settings
    without saving any files to disk.
    """
    # TODO: You need to adjust hyperparameters
    # Extract HOG features from images in the sample directories and 
    # return results and parameters in a dict.
    feature_data = processFiles(pos_dir, neg_dir, recurse=True, hog_features=True)


    # Train SVM and return the classifier and parameters in a dict.
    # This function takes the dict from processFiles() as an input arg.
    classifier_data = trainSVM(feature_data=feature_data)


    # TODO: You need to adjust hyperparameters of loadClassifier() and detectVideo()
    #       to obtain a better performance

    # Instantiate a Detector object and load the dict from trainSVM().
    detector = Detector().loadClassifier(classifier_data=classifier_data)
  
    # Open a VideoCapture object for the video file.
    cap = cv2.VideoCapture(video_file)

    # Start the detector by supplying it with the VideoCapture object.
    # At this point, the video will be displayed, with bounding boxes
    # drawn around detected objects per the method detailed in README.md.
    detector.detectVideo(video_capture=cap)


def experiment2():
    feature_data = processFiles(pos_dir, neg_dir, recurse=True,
                               color_space='YCrCb', hog_bins=24, spatial_size=(20, 20),
                               hog_features=True, hist_features=True, spatial_features=True)
    classifier_data = trainSVM(feature_data=feature_data, C=1000)
    detector = Detector(init_size=(90,90), scale=1.35,
                        x_overlap=0.7, y_step=0.01,
                        x_range=(0, 1), y_range=(0.55, 0.9)).loadClassifier(classifier_data=classifier_data)
    cap = cv2.VideoCapture(video_file)
    detector.detectVideo(video_capture=cap)


def experiment3():
    feature_data = processFiles(pos_dir, neg_dir, recurse=True,
                               color_space='YCrCb', hog_bins=24, spatial_size=(20, 20),
                               hog_features=True)
    classifier_data = trainSVM(feature_data=feature_data, C=1500)
    detector = Detector(init_size=(90,90), scale=1.3,
                        x_overlap=0.75, y_step=0.01,
                        x_range=(0, 1), y_range=(0, 1)).loadClassifier(classifier_data=classifier_data)
    cap = cv2.VideoCapture(video_file)
    detector.detectVideo(video_capture=cap)

if __name__ == "__main__":
    # experiment1()
    experiment2()
    # experiment3()
