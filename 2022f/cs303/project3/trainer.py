import torch
import torch.nn as nn
import tqdm
from torch.utils.data import Dataset

from project3 import MLP  # noqa


data = torch.load("data.pth")
feature, label = data["feature"], data["label"]


class TrajectoryDS(Dataset):
    def __len__(self):
        return len(feature)

    def __getitem__(self, item):
        return feature[item], label[item]


if __name__ == "__main__":
    device = torch.device("cpu")

    model = MLP().to(device)
    dataset = TrajectoryDS()
    criterion = nn.CrossEntropyLoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=0.001)
    train_loader = torch.utils.data.DataLoader(dataset=dataset,
                                               batch_size=64,
                                               shuffle=True)
    test_loader = train_loader

    tr = tqdm.tqdm(range(20))
    for epoch in tr:
        for images, labels in train_loader:
            optimizer.zero_grad()
            images = images.to(device)
            labels = labels.to(device)
            outputs = model(images)
            loss = criterion(outputs, labels)
            loss.backward()
            optimizer.step()
            tr.set_description(f"Epoch: {epoch}, Loss: {loss.item()}")

    torch.save(model, "project3/model.pth")

    correct = 0
    total = 0
    model = torch.load("project3/model.pth")
    with torch.no_grad():
        for images, labels in test_loader:
            images = images.to(device)
            labels = labels.to(device)
            outputs = model(images)
            predictions = torch.argmax(outputs, dim=1)
            total += labels.size(0)
            correct += (predictions == labels).sum().item()

    print(f'Accuracy of the network on the 10000 test images: {100 * correct / total} %')
    print(correct, total)
