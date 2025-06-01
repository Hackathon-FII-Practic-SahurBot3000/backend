import sys
import torch
from torchvision import transforms
from PIL import Image
import json

MODEL_PATH = 'E:/backend/src/main/java/com/sahur_bot_3000/app/aiModel/best_model.pth'
model = torch.load(MODEL_PATH, map_location=torch.device('cpu'))
model.eval()

transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
])

def predict(image_path):
    image = Image.open(image_path).convert('RGB')
    input_tensor = transform(image).unsqueeze(0)
    with torch.no_grad():
        output = model(input_tensor)
        prob = torch.nn.functional.softmax(output[0], dim=0)
        return {"ai": round(float(prob[1]), 4), "not_ai": round(float(prob[0]), 4)}

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(json.dumps({"error": "No image provided"}))
        sys.exit(1)

    image_path = sys.argv[1]
    result = predict(image_path)
    print(json.dumps(result))
